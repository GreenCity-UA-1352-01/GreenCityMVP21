package greencity.notifications;

import greencity.controller.EventController;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.notification.factories.EventUpdateNotificationFactory;
import greencity.service.EventServiceImpl;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.Collections;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventUpdateNotificationFactoryTest {

    @Mock
    private EventServiceImpl eventService;

    @InjectMocks
    private EventUpdateNotificationFactory factory;

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
        updateDto.setId(1L);
        updateDto.setTitle("Eco Life");

        UserVO user = new UserVO();
        user.setId(42L);

        Object[] args = new Object[] {
                updateDto,
                Collections.emptyList(),
                user
        };

        when(eventService.findAttendersIdByEventId(anyLong()))
            .thenReturn(List.of(1L, 2L));

        NotificationRequestDto notification = factory.createEvent(args);

        assertNotNull(notification);
        assertTrue(notification.getAction().contains("Eco Life"));
        assertTrue(notification.getAction().contains("updated"));
        assertTrue(notification.getObjectName().contains("Eco Life"));
        assertEquals(Set.of(1L, 2L), notification.getReceiverIds());
        assertEquals(user.getId(), notification.getInitiatorId());
        assertNotNull(notification.getCreationDate());
    }
}
