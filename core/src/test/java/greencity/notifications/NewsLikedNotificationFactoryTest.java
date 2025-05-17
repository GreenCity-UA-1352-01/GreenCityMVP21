package greencity.notifications;

import greencity.controller.EcoNewsController;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.notification.factories.NewsLikedNotificationFactory;
import greencity.service.EcoNewsService;
import greencity.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsLikedNotificationFactoryTest {
    @Mock
    private EcoNewsService ecoNewsService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NewsLikedNotificationFactory factory;

    @Test
    void testSupports_shouldReturnTrueForCorrectMethod() throws NoSuchMethodException {
        Method method = EcoNewsController.class.getMethod("like", Long.class, UserVO.class);

        boolean result = factory.supports(method);
        assertTrue(result);
    }

    @Test
    void testSupports_shouldReturnFalseForWrongMethod() throws NoSuchMethodException {
        Method method = String.class.getMethod("substring", int.class);

        boolean result = factory.supports(method);
        assertFalse(result);
    }

    @Test
    void testCreateEvent_shouldReturnNotificationRequestDto() {
        Long newsId = 1L;
        UserVO initiator = UserVO.builder().id(2L).name("TestUser").build();
        UserVO receiver = UserVO.builder().id(3L).build();

        EcoNewsVO ecoNewsVO = EcoNewsVO.builder()
                .id(newsId)
                .title("Eco News 1")
                .author(receiver)
                .usersLikedNews(Set.of(initiator))
                .build();

        when(ecoNewsService.findById(newsId)).thenReturn(ecoNewsVO);

        NotificationRequestDto result = factory.createEvent(new Object[] {newsId, initiator});

        assertNotNull(result);
        assertEquals("Eco News 1", result.getObjectName());
        assertIterableEquals(Set.of(receiver.getId()), result.getReceiverIds());
        assertEquals(initiator.getId(), result.getInitiatorId());
        assertTrue(result.getAction().contains("likes your news"));
    }

    @Test
    void testCreateEvent_shouldReturnNullWhenNotLiked() {
        Long newsId = 1L;
        UserVO initiator = UserVO.builder().id(2L).name("TestUser").build();
        UserVO receiver = UserVO.builder().id(3L).build();

        EcoNewsVO ecoNewsVO = EcoNewsVO.builder()
                .id(newsId)
                .title("Some news")
                .author(receiver)
                .usersLikedNews(Set.of())
                .build();

        when(ecoNewsService.findById(newsId)).thenReturn(ecoNewsVO);

        NotificationRequestDto result = factory.createEvent(new Object[]{newsId, initiator});

        assertNull(result);
        verify(notificationService).deleteLikeNewsNotificationIfExists(
                initiator.getId(), receiver.getId(), newsId
        );
    }
}
