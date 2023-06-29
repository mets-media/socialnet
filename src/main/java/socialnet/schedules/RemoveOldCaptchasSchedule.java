package socialnet.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import socialnet.repository.CaptchaRepository;

@Component
@EnableAsync
@RequiredArgsConstructor
public class RemoveOldCaptchasSchedule {
    private final CaptchaRepository captchaRepository;

    @Async
    @Scheduled(cron = "${schedules.deleteOldCaptchasInterval}")
    public void removeOldCaptchas() {
        captchaRepository.removeOldCaptchas();
    }

}
