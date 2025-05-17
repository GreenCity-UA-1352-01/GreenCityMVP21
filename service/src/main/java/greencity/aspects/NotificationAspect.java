package greencity.aspects;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.UnregisteredFactoryException;
import greencity.notification.NotificationHandlerRegistry;
import greencity.notification.NotificationPublisher;
import java.lang.reflect.Method;
import greencity.notification.NotificationEventFactory;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@AllArgsConstructor
public class NotificationAspect {
    private final NotificationPublisher publisher;
    private final NotificationHandlerRegistry registry;

    /**
     * Publishes a notification event after the method marked by {@code @NotifyUser} returns.
     *
     * @param joinPoint is used for annotated method observation.
     * @author Roman Diakov & Rostyslav Zadyraichuk
     * @see greencity.annotations.NotifyUser
     */
    @AfterReturning(value = "@annotation(greencity.annotations.NotifyUser)", argNames = "joinPoint")
    public void afterMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        NotificationEventFactory factory = registry.getFactory(method);
        if (factory == null) {
            throw new UnregisteredFactoryException(ErrorMessage.UNREGISTERED_FACTORY + method);
        }

        publisher.publish(factory.createEvent(args));
    }
}
