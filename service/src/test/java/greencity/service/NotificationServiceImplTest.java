package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.entity.NotificationCounter;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private static final Notification NOTIFICATION;
    private static final NotificationCounter NOTIFICATION_COUNTER;
    private static final NotificationRequestDto NOTIFICATION_REQUEST_DTO;
    private static final NotificationResponseDto NOTIFICATION_RESPONSE_DTO;

    static {
        NOTIFICATION = ModelUtils.getNotification();
        NOTIFICATION_COUNTER = ModelUtils.getNotificationCounter();
        NOTIFICATION_REQUEST_DTO = ModelUtils.getNotificationRequestDto();
        NOTIFICATION_RESPONSE_DTO = ModelUtils.getNotificationResponseDto();
    }

    @Mock
    private NotificationRepo notificationsRepo;
    @Mock
    private NotificationCounterRepo notificationCounterRepo;
    @Mock
    private NotificationMapper notificationMapper;
    @Spy
    private NotificationResponseDtoMapper notificationResponseDtoMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testCreateNotification_whenCounterAlreadyExists_shouldUpdateCounter() {
        NotificationCounter localCounter = ModelUtils.getNotificationCounter();

        when(notificationMapper.convert(NOTIFICATION_REQUEST_DTO)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.findById(NOTIFICATION_REQUEST_DTO.getReceiverId()))
            .thenReturn(Optional.of(localCounter));
        when(notificationResponseDtoMapper.convert(NOTIFICATION)).thenReturn(NOTIFICATION_RESPONSE_DTO);
        when(notificationsRepo.save(NOTIFICATION)).thenReturn(NOTIFICATION);

        NotificationResponseDto actual = notificationService.createNotification(NOTIFICATION_REQUEST_DTO);

        assertEquals(NOTIFICATION_RESPONSE_DTO, actual);
        assertEquals(NOTIFICATION_COUNTER.getCountOfNotifications() + 1, localCounter.getCountOfNotifications());
        verify(notificationCounterRepo, never()).save(any());
    }

    @Test
    void testCreateNotification_whenCounterNotExists_shouldCreateCounter() {
        when(notificationMapper.convert(NOTIFICATION_REQUEST_DTO)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.findById(NOTIFICATION_REQUEST_DTO.getReceiverId()))
            .thenReturn(Optional.empty());
        when(notificationResponseDtoMapper.convert(NOTIFICATION)).thenReturn(NOTIFICATION_RESPONSE_DTO);
        when(notificationsRepo.save(NOTIFICATION)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.save(any())).thenReturn(NOTIFICATION_COUNTER);

        NotificationResponseDto actual = notificationService.createNotification(NOTIFICATION_REQUEST_DTO);

        assertEquals(NOTIFICATION_RESPONSE_DTO, actual);
        verify(notificationCounterRepo).save(any());
    }
}