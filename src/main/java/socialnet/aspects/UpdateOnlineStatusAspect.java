package socialnet.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.model.Person;
import socialnet.model.enums.PersonOnlineStatus;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.stream.Stream;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOnlineStatusAspect {

    private final PersonRepository personRepository;

    private final JwtUtils jwtUtils;

    @AfterReturning(value = "execution(* socialnet.controller.AuthController.login(..))", returning = "retVal")
    public void afterLogin(Object retVal) {
        try {
            long personId = ((CommonRs<PersonRs>) retVal).getData().getId();
            personRepository.updateOnlineStatus(personId, PersonOnlineStatus.ONLINE.name());
            personRepository.updateLastOnlineTime(personId, Timestamp.from(Instant.now()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Around("@annotation(OnlineStatusUpdatable)")
    public Object updateLastOnlineTime(ProceedingJoinPoint joinPoint) {
        try {
            CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
            String[] parameterNames = codeSignature.getParameterNames();
            int parameterNumber = Stream.iterate(0, x -> x < parameterNames.length, x -> x + 1)
                    .filter(x -> parameterNames[x].equals("authorization"))
                    .findFirst()
                    .orElse(-1);
            if (parameterNumber != -1) {
                String userEmail = jwtUtils.getUserEmail((String) joinPoint.getArgs()[parameterNumber]);
                Person person = personRepository.findByEmail(userEmail);
                long personId = person.getId();
                personRepository.updateLastOnlineTime(personId, Timestamp.from(Instant.now()));
                personRepository.updateOnlineStatus(personId, PersonOnlineStatus.ONLINE.name());
            }

            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @After("execution(* socialnet.controller.AuthController.logout(..))")
    public void afterLogout(JoinPoint joinPoint) {
        try {
            String userEmail = jwtUtils.getUserEmail((String) joinPoint.getArgs()[0]);
            Person person = personRepository.findByEmail(userEmail);
            long personId = person.getId();
            personRepository.updateOnlineStatus(personId, PersonOnlineStatus.OFFLINE.name());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
