package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import greencity.entity.Notification;
import greencity.repository.UserRepo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

    private static final Notification NOTIFICATION;
    private static final NotificationRequestDto NOTIFICATION_REQUEST_DTO;

    static {
        NOTIFICATION = ModelUtils.getNotification();
        NOTIFICATION.setId(null);
        NOTIFICATION_REQUEST_DTO = ModelUtils.getNotificationRequestDto();
    }

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private NotificationMapper notificationMapper;

    @Test
    void testConvert() {
        when(userRepo.findById(any())).thenReturn(Optional.of(ModelUtils.getUser()));

        Notification actual = notificationMapper.convert(NOTIFICATION_REQUEST_DTO);

        assertEquals(NOTIFICATION, actual);
        verify(userRepo, times(2)).findById(any());
    }
}