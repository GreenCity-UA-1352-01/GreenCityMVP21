package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.repository.NotificationRepo;
import greencity.entity.NotificationCounter;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private static final NotificationOrigin NOTIFICATION_ORIGIN;
    private static final Notification NOTIFICATION;
    private static final NotificationCounter NOTIFICATION_COUNTER;
    private static final NotificationRequestDto NOTIFICATION_REQUEST_DTO;
    private static final NotificationResponseDto NOTIFICATION_RESPONSE_DTO;
    private static final Pageable PAGEABLE;

    static {
        NOTIFICATION_ORIGIN = NotificationOrigin.GREEN_CITY;
        NOTIFICATION = ModelUtils.getNotification();
        NOTIFICATION_COUNTER = ModelUtils.getNotificationCounter();
        NOTIFICATION_REQUEST_DTO = ModelUtils.getNotificationRequestDto();
        NOTIFICATION_RESPONSE_DTO = ModelUtils.getNotificationResponseDto();
        PAGEABLE = PageRequest.of(0, 10);
    }

    @Mock
    private NotificationRepo notificationRepo;
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
    void testGetAllNotificationsForUser_whenOriginIsNull() {
        Long userId = 1L;
        Notification notification = Notification.builder()
            .id(1L)
            .action("liked")
            .objectName("Test News")
            .creationDate(ZonedDateTime.now())
            .status(NotificationStatus.UNREAD)
            .receiver(ModelUtils.getUser().setId(userId))
            .initiator(ModelUtils.getUser().setId(2L))
            .build();

        NotificationResponseDto dto = NotificationResponseDto.builder()
            .id(1L)
            .action("liked")
            .objectName("Test News")
            .creationDate(notification.getCreationDate())
            .status(NotificationStatus.UNREAD)
            .receiverId(userId)
            .initiatorId(2L)
            .build();
        Page<Notification> page = new PageImpl<>(List.of(notification));

        when(notificationRepo.findNotificationsForUser(userId, PAGEABLE))
            .thenReturn(page);

        PageableDto<NotificationResponseDto> result =
            notificationService.getAllNotificationsForUser(userId, null, PAGEABLE);

        assertThat(result.getPage()).hasSize(1);
        assertThat(result.getPage().getFirst().getId()).isEqualTo(dto.getId());
        verify(notificationRepo).findNotificationsForUser(userId, PAGEABLE);
        verify(notificationRepo, never()).findNotificationsForUser(userId, null, PAGEABLE);
    }

    @Test
    void testGetAllNotificationsForUser_whenOriginIsPresent() {
        Long userId = 1L;
        Notification notification = Notification.builder()
            .id(1L)
            .action("liked")
            .objectName("Test News")
            .creationDate(ZonedDateTime.now())
            .status(NotificationStatus.UNREAD)
            .receiver(ModelUtils.getUser().setId(userId))
            .initiator(ModelUtils.getUser().setId(2L))
            .build();

        NotificationResponseDto dto = NotificationResponseDto.builder()
            .id(1L)
            .action("liked")
            .objectName("Test News")
            .creationDate(notification.getCreationDate())
            .status(NotificationStatus.UNREAD)
            .receiverId(userId)
            .initiatorId(2L)
            .build();
        Page<Notification> page = new PageImpl<>(List.of(notification));

        when(notificationRepo.findNotificationsForUser(userId, NOTIFICATION_ORIGIN, PAGEABLE))
            .thenReturn(page);

        PageableDto<NotificationResponseDto> result =
            notificationService.getAllNotificationsForUser(userId, NOTIFICATION_ORIGIN, PAGEABLE);

        assertThat(result.getPage()).hasSize(1);
        assertThat(result.getPage().getFirst().getId()).isEqualTo(dto.getId());
        verify(notificationRepo).findNotificationsForUser(userId, NOTIFICATION_ORIGIN, PAGEABLE);
        verify(notificationRepo, never()).findNotificationsForUser(userId, PAGEABLE);
    }
}