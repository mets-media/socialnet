package socialnet.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import socialnet.model.Person;
import socialnet.model.enums.PersonOnlineStatus;
import socialnet.repository.PersonRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableAsync
@RequiredArgsConstructor
public class UpdateOnlineStatusScheduler {

    private final PersonRepository personRepository;

    private static final int ONLINE_TIME_TO_LIVE = 1;

    private static final long UPDATE_STATUS_RATE = 10 * 1000L;

    @Async
    @Scheduled(fixedRate = UPDATE_STATUS_RATE)
    public void updateOnlineStatus() {
        List<Person> users = personRepository.findAll();
        for (Person user : users) {
            Timestamp lastOnlineTime = user.getLastOnlineTime();
            if (lastOnlineTime == null) {
                continue;
            }
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().minusMinutes(ONLINE_TIME_TO_LIVE));
            if (lastOnlineTime.before(timestamp)) {
                personRepository.updateOnlineStatus(user.getId(), PersonOnlineStatus.OFFLINE.name());
            }
        }
    }
}
