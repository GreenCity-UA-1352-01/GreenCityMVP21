package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import java.lang.reflect.Method;
import java.util.List;

public interface NotificationEventFactory {
    /**
     * Check if the method is supported by the factory.
     *
     * @param method a method to be checked
     * @return true if the method is supported, false otherwise
     */
    boolean supports(Method method);


    /**
     * Create a notification event based on the given method arguments.
     *
     * @param args the arguments of the method
     * @return a notification event
     */
    List<NotificationRequestDto> createEvent(Object[] args);
}
