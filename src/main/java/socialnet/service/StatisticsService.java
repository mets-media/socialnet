package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.exception.EmptyEmailException;
import socialnet.exception.EntityNotFoundException;
import socialnet.model.*;
import socialnet.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CityRepository cityRepository;

    private final CommentRepository commentRepository;

    private final CountryRepository countryRepository;

    private final DialogsRepository dialogsRepository;

    private final LikeRepository likeRepository;

    private final MessageRepository messageRepository;

    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    private final PersonRepository personRepository;

    private static final String IS_EMPTY = " is empty";

    public Integer getAllCities() {
        return cityRepository.getAllCity();
    }

    public RegionStatisticsRs[] getCitiesUsers() {
        List<RegionStatisticsRs> rs = cityRepository.getCitiesUsers();

        if (!rs.isEmpty()) {
            return rs.toArray(RegionStatisticsRs[]::new);
        }

        return new RegionStatisticsRs[0];
    }

    public Integer getCommentsByPost(Integer postId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Field 'postId' " + postId + IS_EMPTY);
        }
        return commentRepository.findByPostIdCount(Long.valueOf(postId));
    }

    public Integer getCountry() {
        return countryRepository.findAll().size();
    }

    public RegionStatisticsRs[] getCountryUsers() {
        return countryRepository.findCountryUsers().toArray(RegionStatisticsRs[]::new);
    }

    public Integer getDialog() {
        return dialogsRepository.findDialogCount();
    }

    public Integer getDialogsUser(Integer userId) {
        Person person = personRepository.findById(Long.valueOf(userId));
        if (person == null) {
            throw new EntityNotFoundException("Field 'userId' " + userId + IS_EMPTY);
        }
        return dialogsRepository.findDialogsUserCount(userId);
    }

    public Integer getAllLike() {
        return likeRepository.findAllLike();
    }

    public Integer getLikeEntity(Integer entityId) {
        List<Like> like = likeRepository.getLikesByEntityId(entityId);
        if (like.isEmpty()) {
            throw new EmptyEmailException("Field 'entityId' " + entityId + IS_EMPTY);
        } else {
            return likeRepository.getLikesByEntityId(entityId).size();
        }
    }

    public Integer getAllMessage() {
        return messageRepository.getAllMessage();
    }

    public SortedMap<String, Integer> getMessage(Integer firstUserId, Integer secondUserId) {
        final List<Message> messageList = messageRepository.getMessage(firstUserId, secondUserId);
        if (messageList.isEmpty()) {
            throw new EntityNotFoundException("Field 'firstUserId' " + firstUserId
                            + " or 'secondUserId' " + secondUserId + IS_EMPTY);
        } else {
            Map<String, Integer> messages = new HashMap<>();
            Person firstUser = personRepository.findById(Long.valueOf(firstUserId));
            Person secondUser = personRepository.findById(Long.valueOf(secondUserId));
            int counter = 1;
            for (Message message : messageList) {
                if (message.getAuthorId().equals(firstUser.getId()) && message.getRecipientId()
                        .equals(secondUser.getId())) {
                    messages.put(counter + firstUser.getFirstName() + "_" + firstUser.getLastName() + "->"
                                    + secondUser.getFirstName() + "_" + secondUser.getLastName(),
                            Math.toIntExact(message.getId()));
                } else {
                    messages.put(counter + secondUser.getFirstName() + "_" + secondUser.getLastName() + "->"
                                    + firstUser.getFirstName() + "_" + firstUser.getLastName(),
                            Math.toIntExact(message.getId()));
                }
                counter += 1;
            }
            return new TreeMap<>(messages);
        }
    }

    public Integer getMessageByDialog(Integer dialogId) {
        Dialog dialog = dialogsRepository.findByDialogId(Long.valueOf(dialogId));
        if (dialog == null) {
            throw new EntityNotFoundException("Field 'dialogId' " + dialogId + IS_EMPTY);
        } else {
            return messageRepository.getMessageByDialog(dialogId);
        }
    }

    public Integer getAllPost() {
        return postRepository.findAll().size();
    }

    public Integer getAllPostByUser(Integer userId) {
        Person person = personRepository.findById(Long.valueOf(userId));
        if (person == null) {
            throw new EntityNotFoundException("Field 'userId' " + userId + IS_EMPTY);
        } else {
            return postRepository.getAllPostByUser(userId);
        }
    }

    public Integer getAllTags() {
        return tagRepository.getAllTags();
    }

    public Integer getTagsByPost(Integer postId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Field 'postId' " + postId + IS_EMPTY);
        } else {
            return tagRepository.findByPostId(postId.longValue()).size();
        }
    }

    public Integer getAllUsers() {
        return personRepository.findAll().size();
    }

    public Integer getAllUsersByCity(String city) {
        City city1 = cityRepository.getCity(city);
        if (city1 == null) {
            throw new EntityNotFoundException("Field 'city' " + city + IS_EMPTY);
        } else {
            return personRepository.findByCity(city).size();
        }
    }

    public Integer getAllUsersByCountry(String country) {
        Country country1 = countryRepository.getCountry(country);
        if (country1 == null) {
            throw new EntityNotFoundException("Field 'country' " + country + IS_EMPTY);
        } else {
            return personRepository.getAllUsersByCountry(country);
        }
    }
}
