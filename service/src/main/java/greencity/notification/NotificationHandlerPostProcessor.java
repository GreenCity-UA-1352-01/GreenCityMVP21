package greencity.notification;

import greencity.annotations.NotificationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * This class is a spring BeanPostProcessor that is responsible for registering all controllers that has
 * {@link NotificationHandler} annotation and implements {@link NotificationEventFactory} interface to
 * {@link NotificationHandlerRegistry}.
 *
 * <p>
 * Links each controller method marked with {@link greencity.annotations.NotifyUser} to
 * a {@link NotificationEventFactory} implementation.
 *
 * @author Roman Diakov & Rostyslav Zadyraichuk
 */
@Component
@AllArgsConstructor
public class NotificationHandlerPostProcessor implements BeanPostProcessor {
    private final NotificationHandlerRegistry registry;
    private final ApplicationContext context;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof NotificationEventFactory factory
            && bean.getClass().isAnnotationPresent(NotificationHandler.class)) {
            for (Method method : getAllControllerMethods()) {
                if (factory.supports(method)) {
                    registry.register(method, factory);
                }
            }
        }
        return bean;
    }

    private List<Method> getAllControllerMethods() {
        return context.getBeansWithAnnotation(Controller.class).values().stream()
            .flatMap(bean -> Arrays.stream(AopUtils.getTargetClass(bean).getDeclaredMethods()))
            .toList();
    }
}
