package socialnet.service.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String INFO_WITH_RESULT = "Старт метода: {} в классе: {} результат: {}";
    private static final String INFO_WITHOUT_RESULT = "Старт метода: {} в классе: {}";
    private static final String INFO_ERROR = "Ошибка метода: {} в классе: {} результат: {}";

    @Pointcut("execution(* socialnet.controller.*.*(..))")
    public void methodExecutingDebug() {
    }

    @Pointcut("execution(* socialnet.service.*.*(..))")
    public void methodExecutingInfo() {
    }

    @AfterReturning(pointcut = "methodExecutingInfo()", returning = "returningValue")
    public void recordSuccessfulExecutionInfo(JoinPoint joinPoint, Object returningValue) {

        if (joinPoint.getSignature().getName().equals("getUnreadedMessages")) {
            return;
        }

        if (returningValue != null) {
            log.info(INFO_WITH_RESULT,
                joinPoint.getSignature().getName(),
                joinPoint.getSourceLocation().getWithinType().getName(),
                returningValue);
        } else {
            log.info(INFO_WITHOUT_RESULT,
                joinPoint.getSignature().getName(),
                joinPoint.getSourceLocation().getWithinType().getName());
        }
    }

    @AfterReturning(pointcut = "methodExecutingDebug()", returning = "returningValue")
    public void recordSuccessfulExecutionDebug(JoinPoint joinPoint, Object returningValue) {
        if (returningValue != null) {
            log.debug(INFO_WITH_RESULT,
                joinPoint.getSignature().getName(),
                joinPoint.getSourceLocation().getWithinType().getName(),
                returningValue);
        } else {
            log.debug(INFO_WITHOUT_RESULT,
                joinPoint.getSignature().getName(),
                joinPoint.getSourceLocation().getWithinType().getName());
        }
    }

    @AfterThrowing(pointcut = "methodExecutingInfo() || methodExecutingDebug()", throwing = "exception")
    public void recordFailedExecutionInfo(JoinPoint joinPoint, Exception exception) {
        log.error(INFO_ERROR,
            joinPoint.getSignature().getName(),
            joinPoint.getSourceLocation().getWithinType().getName(),
            exception);
    }
}
