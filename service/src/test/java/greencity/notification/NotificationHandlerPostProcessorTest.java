package greencity.notification;

import greencity.annotations.NotificationHandler;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationHandlerPostProcessorTest {

    @Mock
    private NotificationHandlerRegistry registry;
    @Mock
    private ApplicationContext context;
    @Mock
    private NotificationEventFactory notAnnotatedFactory;
    @Mock
    private TestFactory annotatedFactory;

    @InjectMocks
    private NotificationHandlerPostProcessor postProcessor;

    @Test
    void testPostProcessAfterInitialization() throws Exception {
        Method method = TestController.class.getDeclaredMethod("test");

        when(context.getBeansWithAnnotation(Controller.class))
            .thenReturn(mock());
        when(context.getBeansWithAnnotation(Controller.class).values())
            .thenReturn(List.of(new TestController()));
        when(annotatedFactory.supports(method))
            .thenReturn(true);

        postProcessor.postProcessAfterInitialization(annotatedFactory, "beanName");

        verify(registry, times(1)).register(method, annotatedFactory);
    }

    @Test
    void testPostProcessAfterInitialization_whenNotFactoryBean() {
        Object notFactory = mock();

        postProcessor.postProcessAfterInitialization(notFactory, "beanName");

        verifyNoInteractions(notFactory);
    }

    @Test
    void testPostProcessAfterInitialization_whenNotAnnotatedFactoryBean() {
        postProcessor.postProcessAfterInitialization(notAnnotatedFactory, "beanName");

        verifyNoInteractions(context);
    }

    @Test
    void testPostProcessAfterInitialization_whenNoSupportedMethods() {
        when(context.getBeansWithAnnotation(Controller.class))
            .thenReturn(mock());
        when(context.getBeansWithAnnotation(Controller.class).values())
            .thenReturn(List.of(new TestController()));
        when(annotatedFactory.supports(any(Method.class)))
            .thenReturn(false);

        postProcessor.postProcessAfterInitialization(annotatedFactory, "beanName");

        verifyNoInteractions(registry);
    }

    @Controller
    static class TestController {
        public void test() {
        }
    }

    @NotificationHandler
    abstract static class TestFactory implements NotificationEventFactory {
    }

}