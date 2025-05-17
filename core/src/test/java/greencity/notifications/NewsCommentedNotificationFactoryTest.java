package greencity.notifications;

import greencity.controller.EcoNewsCommentController;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.notification.factories.NewsCommentedNotificationFactory;
import greencity.service.EcoNewsService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsCommentedNotificationFactoryTest {
    @Mock
    private EcoNewsService ecoNewsService;

    @InjectMocks
    private NewsCommentedNotificationFactory factory;

    @Test
    void testSupports_shouldReturnTrueForCorrectMethod() throws NoSuchMethodException {
        Method method = EcoNewsCommentController.class.getMethod(
                "save", Long.class, AddEcoNewsCommentDtoRequest.class, UserVO.class);

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
                .build();

        when(ecoNewsService.findById(newsId)).thenReturn(ecoNewsVO);

        NotificationRequestDto result = factory.createEvent(new Object[] {newsId, null, initiator});

        assertNotNull(result);
        assertEquals("Eco News 1", result.getObjectName());
        assertIterableEquals(Set.of(receiver.getId()), result.getReceiverIds());
        assertEquals(initiator.getId(), result.getInitiatorId());
        assertTrue(result.getAction().contains("commented"));
    }
}
