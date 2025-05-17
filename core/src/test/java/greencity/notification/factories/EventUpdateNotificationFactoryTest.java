package greencity.notification.factories;

import greencity.controller.EventController;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class EventUpdateNotificationFactoryTest {

    private EventUpdateNotificationFactory factory;

    @BeforeEach
    void setUp() {
        factory = new EventUpdateNotificationFactory();
    }

    @Test
    void supports_ShouldReturnTrue_ForMatchingMethod() throws NoSuchMethodException {
        Method updateMethod = EventController.class.getMethod(
                "update",
                UpdateEventDtoRequest.class,
                java.util.List.class,
                UserVO.class
        );

        boolean result = factory.supports(updateMethod);
        assertTrue(result, "Factory should support EventController#update method");
    }

    @Test
    void supports_ShouldReturnFalse_ForNonMatchingMethod() throws NoSuchMethodException {
        Method deleteMethod = EventController.class.getMethod(
                "deleteEvent",
                Long.class,
                UserVO.class
        );

        boolean result = factory.supports(deleteMethod);
        assertFalse(result, "Factory should not support EventController#deleteEvent method");
    }

    @Test
    void createEvent_ShouldReturnNotificationDto_WithExpectedFields() {
        UpdateEventDtoRequest updateDto = new UpdateEventDtoRequest();
        updateDto.setTitle("Eco Life");

        UserVO user = new UserVO();
        user.setId(42L);

        Object[] args = new Object[] {
                updateDto,
                Collections.emptyList(),
                user
        };

        NotificationRequestDto notification = factory.createEvent(args);

        assertNotNull(notification);
        assertEquals("event-update", notification.getAction());
        assertTrue(notification.getObjectName().contains("Eco Life"));
        assertEquals(user.getId(), notification.getReceiverId());
        assertEquals(user.getId(), notification.getInitiatorId());
        assertNotNull(notification.getCreationDate());
    }
}
