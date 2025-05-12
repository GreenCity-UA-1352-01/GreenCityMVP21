package greencity.aspects;

import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import greencity.notification.NotificationEventFactory;
import greencity.notification.NotificationHandlerRegistry;
import greencity.notification.NotificationPublisher;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationAspectTest {

    @Mock
    private NotificationPublisher publisher;
    @Mock
    private NotificationHandlerRegistry registry;
    @Mock
    private JoinPoint joinPoint;
    @Mock
    private MethodSignature methodSignature;
    @Mock
    private NotificationEventFactory factory;

    @InjectMocks
    private NotificationAspect aspect;

    @Test
    void testAfterMethodWithList() throws NoSuchMethodException {
        Method testMethod = TestController.class.getMethod("test");
        NotificationRequestDto event = ModelUtils.getNotificationRequestDto();
        List<NotificationRequestDto> eventList = List.of(event);
        Object[] args = new Object[]{};

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(testMethod);
        when(joinPoint.getArgs()).thenReturn(args);
        when(registry.getFactory(testMethod)).thenReturn(factory);
        when(factory.createEvent(args)).thenReturn(eventList);

        aspect.afterMethod(joinPoint);

        verify(publisher).publish(eventList);
    }


    @Controller
    static class TestController {
        public void test() {
        }
    }
}