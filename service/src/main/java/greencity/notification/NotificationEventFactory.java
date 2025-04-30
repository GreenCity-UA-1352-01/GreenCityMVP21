package greencity.notification;

import greencity.dto.notification.NotificationRequestDto;
import java.lang.reflect.Method;

public interface NotificationEventFactory {
    boolean supports(Method method);

    NotificationRequestDto createEvent(Object[] args);
}
