package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.api.response.CommonRs;
import socialnet.api.response.NotificationType;
import socialnet.api.response.PersonRs;
import socialnet.mappers.CommentMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.*;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.CommentServiceDetails;
import socialnet.utils.NotificationPusher;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final PersonSettingRepository personSettingRepository;

    public CommonRs<List<CommentRs>> getComments(Long postId, Integer offset, Integer perPage) {
        List<Comment> commentList = commentRepository.findByPostId(postId, offset, perPage);

        if (commentList.isEmpty()) {
            return new CommonRs<>(Collections.emptyList(), perPage, offset, perPage, System.currentTimeMillis(), 0L);
        }

        List<CommentRs> comments = new ArrayList<>();

        commentList.forEach(comment -> {
            CommentServiceDetails details = getToDTODetails(postId, comment, comment.getId());
            CommentRs commentRs = getCommentRs(comment, details);
            comments.add(commentRs);
        });

        Long total = commentRepository.countCommentsByPostId(postId);

        return new CommonRs<>(comments, perPage, offset, perPage, System.currentTimeMillis(), total);
    }

    private CommentRs getCommentRs(Comment comment, CommentServiceDetails details) {
        CommentRs commentRs = CommentMapper.INSTANCE.toDTO(comment);
        commentRs.setAuthor(details.getAuthor());
        commentRs.setLikes(details.getLikes());
        commentRs.setMyLike(details.getMyLike());
        commentRs.setSubComments(details.getSubComments());

        return commentRs;
    }

    public CommonRs<CommentRs> createComment(CommentRq commentRq, Long postId, String jwtToken) {
        String email = jwtUtils.getUserEmail(jwtToken);
        Person person = personRepository.findByEmail(email);
        CommentServiceDetails toModelDetails = getToModelDetails(person,postId);
        Comment comment = getCommentModel(commentRq, toModelDetails);
        long commentId = commentRepository.save(comment);
        CommentServiceDetails toDTODetails = getToDTODetails(postId, comment, commentId);
        CommentRs commentRs = getCommentRs(comment, toDTODetails);

        Post post = postRepository.findById(postId.intValue());
        PersonSettings personSettingsPostAuthor = personSettingRepository.getSettings(post.getAuthorId());
        if (commentRq.getParentId() != null) {
            Comment comment1 = commentRepository.findById(commentRq.getParentId().longValue());
            PersonSettings personSettingsCommentAuthor = personSettingRepository.getSettings(comment1.getAuthorId());
            if (Boolean.TRUE.equals(personSettingsCommentAuthor.getPostComment()) &&
                    !person.getId().equals(comment1.getAuthorId())) {
                Notification notification = NotificationPusher.
                        getNotification(NotificationType.COMMENT_COMMENT, comment1.getAuthorId(), person.getId());
                NotificationPusher.sendPush(notification, person.getId());
            } else if (!person.getId().equals(post.getAuthorId()) && commentRq.getParentId().longValue() !=
                    (post.getAuthorId()) && Boolean.TRUE.equals(personSettingsPostAuthor.getPostComment())) {
                Notification notification = NotificationPusher.
                        getNotification(NotificationType.POST_COMMENT, post.getAuthorId(), person.getId());
                NotificationPusher.sendPush(notification, person.getId());
                return new CommonRs<>(commentRs, System.currentTimeMillis());
            }
        } else if (Boolean.TRUE.equals(personSettingsPostAuthor.getPostComment()) &&
                !post.getAuthorId().equals(person.getId())) {
            Notification notification = NotificationPusher.
                    getNotification(NotificationType.POST_COMMENT, post.getAuthorId(), person.getId());
            NotificationPusher.sendPush(notification, person.getId());
        }
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }


    private Comment getCommentModel(CommentRq commentRq, CommentServiceDetails details) {
        Comment comment = CommentMapper.INSTANCE.toModel(commentRq);
        comment.setAuthorId(details.getAuthorId());
        comment.setIsBlocked(details.getIsBlocked());
        comment.setIsDeleted(details.getIsDeleted());
        comment.setPostId(details.getPostId());
        comment.setTime(details.getTime());
        comment.setId(details.getId());

        return comment;
    }

    public CommentServiceDetails getToDTODetails(Long postId, Comment comment, long commentId) {
        return new CommentServiceDetails(
                postId,
                comment.getIsBlocked(),
                comment.getIsDeleted(),
                commentId, comment.getAuthorId(),
                findSubComments(postId, commentId),
                likeRepository.getLikesByEntityId(commentId).size()
        );
    }

    private List<CommentRs> findSubComments(Long postId, Long id) {
        List<Comment> comments = commentRepository.findByPostIdParentId(id);
        List<CommentRs> commentRsList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentRs commentRs = getCommentRs(comment, getToDTODetails(postId, comment, comment.getId()));
            commentRsList.add(commentRs);
        }
        return commentRsList;
    }

    private CommentServiceDetails getToModelDetails(Person person,Long postId){
        PersonRs author = PersonMapper.INSTANCE.toDTO(person);
        return new CommentServiceDetails(author, postId);
    }

    public CommonRs<CommentRs> editComment(String jwtToken, Long id, Long commentId, CommentRq commentRq) {
        String email = jwtUtils.getUserEmail(jwtToken);
        Person person = personRepository.findByEmail(email);
        CommentServiceDetails toModelDetails = getToModelDetails(person, id);
        Comment comment = getCommentModel(commentRq, toModelDetails);
        Comment commentFromDB = commentRepository.findById(commentId);
        commentRepository.updateById(comment, commentId);
        commentFromDB.setCommentText(commentRq.getCommentText());
        CommentServiceDetails toDTODetails = getToDTODetails(id, commentFromDB, commentId);
        CommentRs commentRs = getCommentRs(commentFromDB, toDTODetails);
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }

    public CommonRs<CommentRs> deleteComment(Long id, Long commentId) {
        Comment commentFromDB = commentRepository.findById(commentId);
        commentFromDB.setIsDeleted(true);
        commentRepository.updateById(commentFromDB, commentId);
        CommentRs commentRs = getCommentRs(commentFromDB, getToDTODetails(id, commentFromDB, commentId));
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }
    public void hardDeleteComments() {
        List<Comment> deletingComments = commentRepository.findDeletedPosts();
        deletingComments.stream().filter(x -> x.getParentId() != 0).forEach(commentRepository::delete);
        deletingComments.stream().filter(x -> x.getParentId() == 0).forEach(commentRepository::delete);
        List<Like> likes = new ArrayList<>();

        deletingComments.stream()
                .map(dc -> likeRepository.getLikesByEntityId(dc.getId()))
                .forEach(likes::addAll);

        likeRepository.deleteAll(likes);
    }

    public CommonRs<CommentRs> recoverComment(Long id, Long commentId) {
        Comment commentFromDB = commentRepository.findById(commentId);
        commentFromDB.setIsDeleted(false);
        commentRepository.updateById(commentFromDB, commentId);
        CommentRs commentRs = getCommentRs(commentFromDB, getToDTODetails(id, commentFromDB, commentId));
        return new CommonRs<>(commentRs, System.currentTimeMillis());
    }
}
