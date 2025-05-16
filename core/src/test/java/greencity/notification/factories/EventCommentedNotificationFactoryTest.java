package greencity.notification.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import greencity.ModelUtils;
import greencity.controller.EventCommentController;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.CommentDateTimeFormatter;
import greencity.service.EventService;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
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
                .formatted(user.getName(), event.getTitle(), CommentDateTimeFormatter.format(ZonedDateTime.now())))
            .objectName("Event")
            .objectLink("/events/" + event.getId())
            .status(NotificationStatus.UNREAD)
            .creationDate(actual.getCreationDate())
            .receiverId(event.getInitiator().getId())
            .initiatorId(user.getId())
            .origin(NotificationOrigin.GREEN_CITY)
            .build();

        assertEquals(expected, actual);
    }
}