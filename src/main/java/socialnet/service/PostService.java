package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.PostRq;
import socialnet.api.response.*;
import socialnet.exception.EntityNotFoundException;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.PostMapper;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.NotificationPusher;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final JwtUtils jwtUtils;
    private final FriendsShipsRepository friendsShipsRepository;
    private final PersonSettingRepository personSettingRepository;

    public CommonRs<List<PostRs>> getFeeds(String jwtToken, int offset, int perPage) {
        List<Post> postList = postRepository.findAll(offset, perPage, System.currentTimeMillis());
        List<PostRs> postRsList = new ArrayList<>();
        long total = postRepository.getAllCountNotDeleted();

        for (Post post : postList) {
            int postId = post.getId().intValue();
            PostServiceDetails details = getDetails(post.getAuthorId(), postId, jwtToken);
            PostRs postRs = setPostRs(post, details);
            postRsList.add(postRs);
        }

        return new CommonRs<>(postRsList, perPage, offset, perPage, System.currentTimeMillis(), total);
    }

    PostServiceDetails getDetails(long authorId, int postId, String jwtToken) {
        Person author = getAuthor(authorId);
        List<Like> likes = getLikes(postId).stream().filter(l -> l.getType().equals("Post")).collect(Collectors.toList());
        List<Tag> tags = getTags(postId);
        List<String> tagsStrings = tags.stream().map(Tag::getTag).collect(Collectors.toList());
        Person authUser = getAuthUser(jwtToken);
        List<Comment> postComments = getPostComments(postId);
        List<CommentRs> comments = getComments(postComments, jwtToken);
        comments = comments.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
        return new PostServiceDetails(author, likes, tagsStrings, authUser.getId(), comments);
    }

    public static PostRs setPostRs(Post post2, PostServiceDetails details1) {
        PostRs postRs = PostMapper.INSTANCE.toDTO(post2);
        postRs.setAuthor(PersonMapper.INSTANCE.toDTO(details1.getAuthor()));
        postRs.setComments(details1.getComments());
        postRs.setLikes(details1.getLikes().size());
        postRs.setMyLike(itLikesMe(details1.getLikes(), details1.getAuthUserId()));
        postRs.setTags(details1.getTags());

        return postRs;
    }

    public static boolean itLikesMe(List<Like> likes, long authUserId) {
        for (Like like : likes) {
            if (like.getPersonId().equals(authUserId)) return true;
        }
        return false;
    }

    private List<CommentRs> getComments(List<Comment> postComments, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (Comment postComment : postComments) {
            int commentId = postComment.getId().intValue();
            Person author = getAuthor(postComment.getAuthorId());
            List<Comment> subCommentsList = getSubCommentList(postComment.getId());
            assert subCommentsList != null;
            List<CommentRs> subComments = getSubComments(subCommentsList, jwtToken);
            List<Like> likes = getLikes(commentId).stream().filter(l -> l.getType().equals("Comment")).collect(Collectors.toList());
            Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = getCommentRs(author, postComment, subComments, likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    private CommentRs getCommentRs(Person author, Comment comment, List<CommentRs> subComments, List<Like> likes, long authUserId) {
        CommentRs commentRs = CommentMapper.INSTANCE.toDTO(comment);
        commentRs.setAuthor(PersonMapper.INSTANCE.toDTO(author));
        commentRs.setLikes(likes.size());
        commentRs.setMyLike(itLikesMe(likes, authUserId));
        commentRs.setSubComments(subComments);

        return commentRs;
    }

    private List<CommentRs> getSubComments(List<Comment> parentCommentsList, String jwtToken) {
        List<CommentRs> comments = new ArrayList<>();
        for (Comment parentComment : parentCommentsList) {
            int commentId = parentComment.getId().intValue();
            Person author = getAuthor(parentComment.getAuthorId());
            List<Comment> subCommentsList = getSubCommentList(parentComment.getId());
            assert subCommentsList != null;
            List<Like> likes = getLikes(commentId).stream().filter(l -> l.getType().equals("Comment")).collect(Collectors.toList());
            Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
            long authUserId = authUser.getId();
            CommentRs commentRs = getCommentRs(author, parentComment, new ArrayList<>(), likes, authUserId);
            comments.add(commentRs);
        }
        return comments;
    }

    private List<Comment> getSubCommentList(long parentId) {
        return commentRepository.findByPostIdParentId(parentId);
    }

    public CommonRs<PostRs> createPost(PostRq postRq, int id, Long publishDate, String jwtToken) {
        Post post = setPost(postRq, publishDate, id);
        int postId = postRepository.save(post);
        tagRepository.saveAll(postRq.getTags(), postId);
        Person author = personRepository.findById((long) id);
        PostServiceDetails details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = setPostRs(post, details);
        List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(author.getId());
        sendAllFriendShips(allFriendships, author.getId());
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    private Post setPost(PostRq postRq, Long publishDate, int id) {
        Post post = PostMapper.INSTANCE.postRqToPost(postRq);
        post.setAuthorId((long) id);
        post.setTime(getTime(publishDate));

        return post;
    }

    Timestamp getTime(Long publishDate) {
        if (publishDate == null) return new Timestamp(System.currentTimeMillis());
        return new Timestamp(publishDate);
    }

    public CommonRs<PostRs> getPostById(int postId, String jwtToken) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post with id = " + postId + " not found");
        }

        Person author = getAuthor(post.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = setPostRs(post, details);
        postRs.setTags(tagRepository.findByPostId((long) postId).stream().map(Tag::getTag).collect(Collectors.toList()));
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    private Person getAuthor(long id) {
        return personRepository.findById(id);
    }

    private List<Like> getLikes(int id) {
        return likeRepository.getLikesByEntityId(id);
    }

    private List<Tag> getTags(int id) {
        return tagRepository.findByPostId((long) id);
    }

    private Person getAuthUser(String jwtToken) {
        return personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
    }

    private List<Comment> getPostComments(int id) {
        return commentRepository.findByPostId((long) id);
    }

    public CommonRs<PostRs> updatePost(int id, PostRq postRq, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        long publishDate = postFromDB.getTime().getTime();
        Post post = setPost(postRq, publishDate, id);
        postRepository.updateById(id, post);
        tagRepository.deleteAll(tagRepository.findByPostId((long) id));
        tagRepository.saveAll(postRq.getTags(), id);
        Post newPost = postRepository.findById(id);
        Person author = getAuthor(newPost.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), newPost.getId().intValue(), jwtToken);
        PostRs postRs = setPostRs(newPost, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> markAsDelete(int postId, String jwtToken) {
        postRepository.markAsDeleteById(postId);

        Post postFromDB = postRepository.findById(postId);
        Person author = getAuthor(postFromDB.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), postId, jwtToken);
        PostRs postRs = setPostRs(postFromDB, details);

        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public CommonRs<PostRs> recoverPost(int id, String jwtToken) {
        Post postFromDB = postRepository.findById(id);
        postFromDB.setIsDeleted(false);
        postFromDB.setTimeDelete(null);
        postRepository.updateById(id, postFromDB);
        Person author = getAuthor(postFromDB.getAuthorId());
        PostServiceDetails details = getDetails(author.getId(), postFromDB.getId().intValue(), jwtToken);
        PostRs postRs = setPostRs(postFromDB, details);
        return new CommonRs<>(postRs, System.currentTimeMillis());
    }

    public void hardDeletingPosts() {
        List<Post> deletingPosts = postRepository.findDeletedPosts();
        List<Tag> tags = new ArrayList<>();
        List<Like> likes = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        List<Comment> commentsByComments = new ArrayList<>();
        for (Post deletingPost : deletingPosts) {
            tags.addAll(tagRepository.findByPostId(deletingPost.getId()));
            likes.addAll(likeRepository.getLikesByEntityId(deletingPost.getId()));
            comments.addAll(commentRepository.findByPostId(deletingPost.getId()));
            for (Comment comment : comments) {
                likes.addAll(likeRepository.getLikesByEntityId(comment.getId()));
                commentsByComments.addAll(commentRepository.findByPostIdParentId(comment.getId()));
            }
            for (Comment commentsByComment : commentsByComments) {
                likes.addAll(likeRepository.getLikesByEntityId(commentsByComment.getId()));
            }
        }
        tagRepository.deleteAll(tags);
        likeRepository.deleteAll(likes);
        commentRepository.deleteAll(commentsByComments);
        commentRepository.deleteAll(comments);
        deletingPosts.forEach(p -> postRepository.deleteById(p.getId().intValue()));
    }

    public CommonRs<List<PostRs>> getFeedsByAuthorId(Long authorId, String jwtToken, Integer offset, Integer perPage) {
        List<PostRs> postRsList = new ArrayList<>();
        List<Post> postList = postRepository.findPostsByUserId(authorId, offset, perPage);

        if (postList.isEmpty()) {
            return new CommonRs<>(postRsList, perPage, offset, perPage, System.currentTimeMillis(), 0L);
        }

        long total = postRepository.countPostsByUserId(authorId);

        for (Post post : postList) {
            int postId = post.getId().intValue();
            PostServiceDetails details = getDetails(authorId, postId, jwtToken);
            PostRs postRs = setPostRs(post, details);
            postRsList.add(postRs);
        }

        return new CommonRs<>(postRsList, perPage, offset, perPage, System.currentTimeMillis(), total);
    }

    private void sendAllFriendShips(List<Friendships> list, Long id) {
        for (Friendships friendships : list) {
            Notification notification = null;
            PersonSettings settingsSrc = personSettingRepository.getSettings(friendships.getSrcPersonId());
            PersonSettings settingsDst = personSettingRepository.getSettings(friendships.getDstPersonId());

            if (!id.equals(friendships.getDstPersonId()) && Boolean.TRUE.equals(settingsDst.getPost())) {
                notification = NotificationPusher.getNotification(NotificationType.POST, friendships.getDstPersonId(), id);
            } else if (Boolean.TRUE.equals(settingsSrc.getPost())) {
                notification = NotificationPusher.getNotification(NotificationType.POST, friendships.getSrcPersonId(), id);
            }

            if (notification != null) {
                NotificationPusher.sendPush(notification, id);
            }
        }
    }
}
