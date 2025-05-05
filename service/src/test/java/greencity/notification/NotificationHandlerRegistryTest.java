package greencity.notification;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationHandlerRegistryTest {

    @Mock
    private Method method;
    @Mock
    private NotificationEventFactory factory;

    @InjectMocks
    private NotificationHandlerRegistry registry;

    @Test
    void testRegister_whenNewMethod_shouldPutToMap() {
        registry.register(method, factory);

        assertEquals(factory, registry.getFactory(method));
    }

    @Test
    void testRegister_whenExistingMethod_shouldThrowException() {
        registry.register(method, factory);

        assertThrows(IllegalStateException.class, () -> registry.register(method, factory));
    }

    @Test
    void testGetFactory_whenExistingMethod_shouldReturnFactory() {
        NotificationEventFactory expected = factory;
        registry.register(method, expected);

        NotificationEventFactory actual = registry.getFactory(method);

        assertEquals(expected, actual);
    }

    @Test
    void testGetFactory_whenNonExistingMethod_shouldReturnNull() {
        assertNull(registry.getFactory(method));
    }
}