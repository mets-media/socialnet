package socialnet.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import socialnet.api.response.NotificationType;
import socialnet.model.Friendships;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.model.PersonSettings;
import socialnet.repository.FriendsShipsRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PersonSettingRepository;
import socialnet.utils.NotificationPusher;

import java.util.List;

@Component
@EnableAsync
@RequiredArgsConstructor
public class BirthdayNotifications {

    private final PersonRepository personRepository;
    private final FriendsShipsRepository friendsShipsRepository;
    private final PersonSettingRepository personSettingRepository;

    @Async
    @Scheduled(cron = "${schedules.birthDatePush}")
    public void sendNotificationOfBirthday() {
        List<Person> personsByBirthDate = personRepository.findPersonsByBirthDate();
        if (!personsByBirthDate.isEmpty()) {
            for (Person person : personsByBirthDate) {
                List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(person.getId());
                sendAllFriendShips(allFriendships, person.getId());
            }
        }
    }

    private void sendAllFriendShips(List<Friendships> list, Long id) {
        for (Friendships friendships : list) {
            Notification notification = null;
            PersonSettings settingsDst = personSettingRepository.getSettings(friendships.getDstPersonId());
            PersonSettings settingsSrc = personSettingRepository.getSettings(friendships.getSrcPersonId());
            if (!id.equals(friendships.getDstPersonId()) && Boolean.TRUE.equals(settingsDst.getFriendBirthday())) {
                notification = NotificationPusher.getNotification(NotificationType.FRIEND_BIRTHDAY, friendships.getDstPersonId(), id);
            } else if (Boolean.TRUE.equals(settingsSrc.getFriendBirthday())) {
                notification = NotificationPusher.getNotification(NotificationType.FRIEND_BIRTHDAY, friendships.getSrcPersonId(), id);
            }
            if (notification != null) {
                NotificationPusher.sendPush(notification, id);
            }
        }
    }
}
