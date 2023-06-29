package socialnet.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import socialnet.api.response.NotificationRs;
import socialnet.api.response.NotificationType;
import socialnet.api.response.PersonRs;
import socialnet.mappers.NotificationMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.repository.NotificationRepository;
import socialnet.repository.PersonRepository;
import socialnet.service.TelegramBotService;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@Slf4j
public class NotificationPusher {
    private static SimpMessagingTemplate messagingTemplate;
    private static PersonRepository personRepository;
    private static NotificationRepository repository;
    private static TelegramBotService telegramBotService;

    public NotificationPusher(
        SimpMessagingTemplate simpMessagingTemplate,
        PersonRepository personRepository,
        NotificationRepository notificationRepository,
        TelegramBotService telegramBotService)
    {
        NotificationPusher.repository = notificationRepository;
        NotificationPusher.personRepository = personRepository;
        NotificationPusher.messagingTemplate = simpMessagingTemplate;
        NotificationPusher.telegramBotService = telegramBotService;
    }

    public static void sendPush(Notification notification, Long personId) {
        telegramBotService.notificate(notification);
        Long id = repository.saveNotification(notification);
        notification.setId(id);
        Person personReceiver = personRepository.findById(personId);
        PersonRs personRs = PersonMapper.INSTANCE.toDTO(personReceiver);
        NotificationRs dto = NotificationMapper.INSTANCE.toDTO(notification);
        dto.setEntityAuthor(personRs);
        try {
            messagingTemplate.convertAndSend(String.format("/user/%s/queue/notifications", notification.getPersonId()),
                    dto);
        } catch (Exception e) {
            log.debug("exception in sending push notification!");
        }
    }
    public static Notification getNotification(NotificationType type,Long personId,Long entityId) {
        Notification notification = new Notification();
        notification.setSentTime(Timestamp.from(Instant.now()));
        notification.setNotificationType(type.toString());
        notification.setContact(personId.toString());
        notification.setIsRead(false);
        notification.setPersonId(personId);
        notification.setEntityId(entityId);
        return notification;
    }


}
