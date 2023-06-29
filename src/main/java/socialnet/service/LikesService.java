package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;
import socialnet.api.response.NotificationType;
import socialnet.model.*;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.*;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.NotificationPusher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikeRepository likeRepository;
    private final PersonRepository personRepository;
    private final JwtUtils jwtUtils;
    private final PostRepository postRepository;
    private final PersonSettingRepository personSettingRepository;
    private final CommentRepository commentRepository;

    private final FriendsShipsRepository friendsShipsRepository;

    public CommonRs<LikeRs> getLikes(Integer itemId, String type) {
        List<Like> likes = likeRepository.getLikesByEntityId(itemId);
        likes = likes.stream().filter(l -> l.getType().equals(type)).collect(Collectors.toList());
        List<Integer> users = new ArrayList<>();
        likes.forEach(l -> users.add(l.getPersonId().intValue()));
        return new CommonRs<>(new LikeRs(likes.size(), users), System.currentTimeMillis());
    }

    public CommonRs<LikeRs> putLike(String jwtToken, LikeRq likeRq) {
        Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
        Like like = new Like();
        like.setType(likeRq.getType());
        like.setEntityId(likeRq.getItemId().longValue());
        like.setPersonId(authUser.getId());
        if (!isBlocked(authUser, likeRq)) {
            likeRepository.save(like);
        }
        List<Like> likes = likeRepository.getLikesByEntityId(likeRq.getItemId());
        likes = likes.stream().filter(l -> l.getType().equals(likeRq.getType())).collect(Collectors.toList());
        List<Integer> users = new ArrayList<>();
        likes.forEach(l -> users.add(l.getPersonId().intValue()));

        for (Like l : likes) {
            users.add(l.getPersonId().intValue());
        }
        if (likeRq.getType().equals("Comment")) {
            Comment comment = commentRepository.findById(likeRq.getItemId().longValue());
            PersonSettings personSettings = personSettingRepository.getSettings(comment.getAuthorId());

            if (Boolean.TRUE.equals(personSettings.getPostLike()) && !comment.getAuthorId().equals(authUser.getId())) {
                Notification notification = NotificationPusher.getNotification(NotificationType.POST_LIKE,
                        comment.getAuthorId(), authUser.getId());
                NotificationPusher.sendPush(notification, authUser.getId());
            }
        } else {
            Post post = postRepository.findById(likeRq.getItemId());
            PersonSettings personSettings = personSettingRepository.getSettings(post.getAuthorId());
            if (Boolean.TRUE.equals(personSettings.getPostLike()) && !post.getAuthorId().equals(authUser.getId())) {
                Notification notification = NotificationPusher.getNotification(NotificationType.POST_LIKE,
                        post.getAuthorId(), authUser.getId());
                NotificationPusher.sendPush(notification, authUser.getId());
            }
        }
        return new CommonRs<>(new LikeRs(likes.size(), users), System.currentTimeMillis());
    }

    private boolean isBlocked(Person authUser, LikeRq likeRq) {
        if (likeRq.getType().equals("Post")) {
            Post post = postRepository.findById(likeRq.getItemId());
            Person person = personRepository.findById(post.getAuthorId());
            Friendships friendships = friendsShipsRepository.getFriendStatusBlocked(authUser.getId(), person.getId());
            if (friendships == null) return false;
            return friendships.getStatusName().equals(FriendshipStatusTypes.BLOCKED);
        }
        if (likeRq.getType().equals("Comment")) {
            Comment comment = commentRepository.findById(likeRq.getItemId().longValue());
            Person person = personRepository.findById(comment.getAuthorId());
            Friendships friendships = friendsShipsRepository.getFriendStatusBlocked(authUser.getId(), person.getId());
            if (friendships == null) return false;
            return friendships.getStatusName().equals(FriendshipStatusTypes.BLOCKED);
        }
        return false;
    }

    public CommonRs<LikeRs> deleteLike(String jwtToken, Integer itemId, String type) {
        Person authUser = personRepository.findByEmail(jwtUtils.getUserEmail(jwtToken));
        List<Like> likes = likeRepository.getLikesByEntityId(itemId);
        likes = likes.stream().filter(l -> l.getType().equals(type)).collect(Collectors.toList());
        for (Like like : likes) {
            if (like.getPersonId().equals(authUser.getId())) {
                likeRepository.delete(like);
                break;
            }
        }
        List<Like> likesAfterDelete = likeRepository.getLikesByEntityId(itemId);
        List<Integer> users = new ArrayList<>();
        likes.forEach(l -> users.add(l.getPersonId().intValue()));
        return new CommonRs<>(new LikeRs(likesAfterDelete.size(), users), System.currentTimeMillis());
    }
}
