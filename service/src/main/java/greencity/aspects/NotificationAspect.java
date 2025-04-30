package greencity.aspects;

import greencity.dto.notification.NotificationRequestDto;
import greencity.notification.NotificationEventFactory;
import java.lang.reflect.Method;
import java.util.List;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Aspect
@AllArgsConstructor
public class NotificationAspect {
    private final ApplicationEventPublisher publisher;
    private final List<NotificationEventFactory> notificationFactories;

    @AfterReturning(value = "@annotation(greencity.annotations.NotifyUser)", argNames = "joinPoint")
    public void afterMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        notificationFactories.stream()
            .filter(f -> f.supports(method))
            .findFirst()
            .ifPresent(factory -> {
                NotificationRequestDto event = factory.createEvent(args);
                publisher.publishEvent(event);
            });
    }
}
