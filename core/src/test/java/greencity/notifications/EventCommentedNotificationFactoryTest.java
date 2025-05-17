package greencity.notifications;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import greencity.ModelUtils;
import greencity.controller.EventCommentController;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.notification.factories.EventCommentedNotificationFactory;
import greencity.service.EventService;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventCommentedNotificationFactoryTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventCommentedNotificationFactory eventCommentedNotificationFactory;

    @Test
    void testSupports() throws Exception {
        Class<?>[] parameterTypes = {Long.class, AddEventCommentDtoRequest.class, UserVO.class};
        Method method = EventCommentController.class.getDeclaredMethod("save", parameterTypes);

        assertTrue(eventCommentedNotificationFactory.supports(method));
    }

    @Test
    void testSupports_whenMethodIsNotSave() throws Exception {
        Method method = this.getClass().getDeclaredMethod("testSupports");

        assertFalse(eventCommentedNotificationFactory.supports(method));
    }

    @Test
    void testCreateEvent() {
        UserVO user = ModelUtils.getUserVO();
        EventVO event = ModelUtils.getEventVO();

        when(eventService.findById(event.getId())).thenReturn(event);

        NotificationRequestDto actual = eventCommentedNotificationFactory.createEvent(
            new Object[]{event.getId(), null, user});
        NotificationRequestDto expected = NotificationRequestDto.builder()
            .action("%s commented on your event %s. %s"
                .formatted(user.getName(), event.getTitle(), NotificationDateTimeFormatter.format(ZonedDateTime.now())))
            .objectName("title")
            .creationDate(actual.getCreationDate())
            .receiverIds(Set.of(event.getInitiator().getId()))
            .initiatorId(user.getId())
            .objectType(NotificationObjectType.EVENT)
            .notificationType(NotificationType.EVENT_COMMENT)
            .origin(NotificationOrigin.GREEN_CITY)
            .build();

        assertEquals(expected, actual);
    }
}