package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.enums.NotificationStatus;
import greencity.repository.NotificationRepo;
import greencity.entity.NotificationCounter;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.enums.NotificationStatus;
import greencity.repository.NotificationCounterRepo;
import greencity.service.NotificationServiceImpl;
import greencity.repository.NotificationRepo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    private NotificationRepo notificationRepo;
    @Mock
    private NotificationCounterRepo notificationCounterRepo;
    @Mock
    private NotificationMapper notificationMapper;
    @Spy
    private NotificationResponseDtoMapper notificationResponseDtoMapper;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testCreateNotification_whenCounterAlreadyExists_shouldUpdateCounter() {
        NotificationCounter localCounter = ModelUtils.getNotificationCounter();

        when(notificationMapper.convert(NOTIFICATION_REQUEST_DTO)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.findById(NOTIFICATION_REQUEST_DTO.getReceiverId()))
            .thenReturn(Optional.of(localCounter));
        when(notificationResponseDtoMapper.convert(NOTIFICATION)).thenReturn(NOTIFICATION_RESPONSE_DTO);
        when(notificationRepo.save(NOTIFICATION)).thenReturn(NOTIFICATION);

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
        when(notificationRepo.save(NOTIFICATION)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.save(any())).thenReturn(NOTIFICATION_COUNTER);

        NotificationResponseDto actual = notificationService.createNotification(NOTIFICATION_REQUEST_DTO);

        assertEquals(NOTIFICATION_RESPONSE_DTO, actual);
        verify(notificationCounterRepo).save(any());
    }

    @Test
    void testGetAllNotificationsForUser_returnsMappedDtos() {
        Long userId = 1L;
        Notification notification = Notification.builder()
            .id(1L)
            .action("liked")
            .objectName("Test News")
            .creationDate(ZonedDateTime.now())
            .status(NotificationStatus.UNREAD)
            .build();

        NotificationResponseDto dto = NotificationResponseDto.builder()
            .id(1L)
            .action("liked")
            .objectName("Test News")
            .creationDate(notification.getCreationDate())
            .status("UNREAD")
            .receiverId(userId)
            .initiatorId(2L)
            .build();

        when(notificationRepo.findAllByReceiverIdOrderByCreationDateDesc(userId))
            .thenReturn(List.of(notification));
        when(modelMapper.map(notification, NotificationResponseDto.class)).thenReturn(dto);

        List<NotificationResponseDto> result = notificationService.getAllNotificationsForUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(dto.getId());
        verify(notificationRepo).findAllByReceiverIdOrderByCreationDateDesc(userId);
        verify(modelMapper).map(notification, NotificationResponseDto.class);
    }
}