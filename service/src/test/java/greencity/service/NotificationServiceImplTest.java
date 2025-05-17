package greencity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import greencity.mapping.NotificationsGroupedResponseDtoMapper;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.repository.NotificationRepo;
import greencity.entity.NotificationCounter;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZonedDateTime;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.enums.NotificationStatus;
import greencity.repository.NotificationRepo;
import greencity.repository.NotificationCounterRepo;
import greencity.service.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final NotificationOrigin NOTIFICATION_ORIGIN;
    private static final Notification NOTIFICATION;
    private static final NotificationCounter NOTIFICATION_COUNTER;
    private static final NotificationRequestDto NOTIFICATION_REQUEST_DTO;
    private static final NotificationResponseDto NOTIFICATION_RESPONSE_DTO;
    private static final NotificationsGroupedResponseDto GROUPED_RESPONSE_DTO;

    static {
        NOTIFICATION_ORIGIN = NotificationOrigin.GREEN_CITY;
        NOTIFICATION = ModelUtils.getNotification();
        NOTIFICATION_COUNTER = ModelUtils.getNotificationCounter();
        NOTIFICATION_REQUEST_DTO = ModelUtils.getNotificationRequestDto();
        NOTIFICATION_RESPONSE_DTO = ModelUtils.getNotificationResponseDto();
        GROUPED_RESPONSE_DTO = ModelUtils.getNotificationsGroupedResponseDto();
    }

    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private NotificationCounterRepo notificationCounterRepo;
    @Mock
    private NotificationMapper notificationMapper;
    @Spy
    private NotificationResponseDtoMapper notificationResponseDtoMapper;
    @Spy
    private NotificationsGroupedResponseDtoMapper notificationsGroupedResponseDtoMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void testCreateNotification_whenCounterAlreadyExists_shouldUpdateCounter() {
        NotificationCounter localCounter = ModelUtils.getNotificationCounter();

        when(notificationMapper.convert(NOTIFICATION_REQUEST_DTO)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.findById(anyLong())).thenReturn(Optional.of(localCounter));
        when(notificationRepo.save(NOTIFICATION)).thenReturn(NOTIFICATION);

        List<NotificationResponseDto> actual = notificationService.createNotifications(NOTIFICATION_REQUEST_DTO);

        assertIterableEquals(List.of(NOTIFICATION_RESPONSE_DTO), actual);
        assertEquals(NOTIFICATION_COUNTER.getCountOfNotifications() + 1, localCounter.getCountOfNotifications());
        verify(notificationCounterRepo, never()).save(any());
    }

    @Test
    void testCreateNotification_whenCounterNotExists_shouldCreateCounter() {
        when(notificationMapper.convert(NOTIFICATION_REQUEST_DTO)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.findById(anyLong())).thenReturn(Optional.empty());
        when(notificationRepo.save(NOTIFICATION)).thenReturn(NOTIFICATION);
        when(notificationCounterRepo.save(any())).thenReturn(NOTIFICATION_COUNTER);

        List<NotificationResponseDto> actual = notificationService.createNotifications(NOTIFICATION_REQUEST_DTO);

        assertIterableEquals(List.of(NOTIFICATION_RESPONSE_DTO), actual);
        verify(notificationCounterRepo).save(any());
    }

    @Test
    void testGetAllNotificationsForUser_whenEmptyNotifications_shouldReturnEmptySet() {
        when(notificationRepo.findNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN))
            .thenReturn(Collections.emptyList());

        Set<BaseNotificationResponseDto> actual = notificationService
            .getAllNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);

        assertTrue(actual.isEmpty());
        verify(notificationRepo).findNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);
        verify(notificationResponseDtoMapper, never()).convert(NOTIFICATION);
        verify(notificationsGroupedResponseDtoMapper, never()).convert(List.of(NOTIFICATION_RESPONSE_DTO));
    }

    @Test
    void testGetAllNotificationsForUser_shouldReturnSortedResults() {
        ZonedDateTime now = ZonedDateTime.now();
        Notification notificationForSorting = ModelUtils.getNotification()
            .setNotificationType(NotificationType.COMMENT_LIKE)
            .setCreationDate(now);
        BaseNotificationResponseDto notificationForSortingDto = ModelUtils.getNotificationResponseDto()
            .setNotificationType(NotificationType.COMMENT_LIKE)
            .setCreationDate(now);
        NotificationsGroupedResponseDto groupedNotificationForSortingDto = notificationsGroupedResponseDtoMapper.convert(
            List.of((NotificationResponseDto) notificationForSortingDto)
        );

        when(notificationRepo.findNotificationsForUser(anyLong(), any(NotificationOrigin.class)))
            .thenReturn(List.of(NOTIFICATION, notificationForSorting));

        Set<BaseNotificationResponseDto> actual = notificationService
            .getAllNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);

        assertEquals(2, actual.size());
        Iterator<BaseNotificationResponseDto> iterator = actual.iterator();
        assertEquals(groupedNotificationForSortingDto, iterator.next());
        assertEquals(GROUPED_RESPONSE_DTO, iterator.next());
        verify(notificationRepo).findNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);
        verify(notificationResponseDtoMapper, atLeast(1)).convert(any(Notification.class));
        verify(notificationsGroupedResponseDtoMapper, atLeast(1)).convert(anyList());
    }

    @Test
    void testGetAllNotificationsForUser_shouldReturnGroupedResults() {
        ZonedDateTime now = ZonedDateTime.now();
        Notification notificationForGrouping = ModelUtils.getNotification()
            .setCreationDate(now);
        BaseNotificationResponseDto notificationForGroupingDto = ModelUtils.getNotificationResponseDto()
            .setCreationDate(now);
        NotificationsGroupedResponseDto groupedNotificationForGroupingDto = notificationsGroupedResponseDtoMapper.convert(
            List.of((NotificationResponseDto) notificationForGroupingDto)
        );

        when(notificationRepo.findNotificationsForUser(anyLong(), any(NotificationOrigin.class)))
            .thenReturn(List.of(NOTIFICATION, notificationForGrouping));

        Set<BaseNotificationResponseDto> actual = notificationService
            .getAllNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);

        assertEquals(1, actual.size());
        assertEquals(NotificationsGroupedResponseDto.class, actual.toArray()[0].getClass());
        Iterator<BaseNotificationResponseDto> iterator = actual.iterator();
        BaseNotificationResponseDto actualGrouped = iterator.next();
        assertNotEquals(GROUPED_RESPONSE_DTO, actualGrouped);
        assertEquals(groupedNotificationForGroupingDto, actualGrouped);
        verify(notificationRepo).findNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);
        verify(notificationResponseDtoMapper, atLeast(1)).convert(any(Notification.class));
        verify(notificationsGroupedResponseDtoMapper, atLeast(1)).convert(anyList());
    }

    @Test
    void testGetAllNotificationsForUser_shouldReturnMixedResults() {
        Notification nonGroupableNotification = ModelUtils.getNotification()
            .setNotificationType(NotificationType.EVENT_UPDATE_NAME);

        when(notificationRepo.findNotificationsForUser(anyLong(), any(NotificationOrigin.class)))
            .thenReturn(List.of(NOTIFICATION, nonGroupableNotification));

        Set<BaseNotificationResponseDto> actual = notificationService
            .getAllNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);
        List<? extends Class<? extends BaseNotificationResponseDto>> actualClasses = actual.stream()
            .map(BaseNotificationResponseDto::getClass)
            .toList();

        assertEquals(2, actual.size());
        assertTrue(actualClasses.contains(NotificationResponseDto.class));
        assertTrue(actualClasses.contains(NotificationsGroupedResponseDto.class));
        verify(notificationRepo).findNotificationsForUser(USER_ID, NOTIFICATION_ORIGIN);
        verify(notificationResponseDtoMapper, atLeast(1)).convert(any(Notification.class));
        verify(notificationsGroupedResponseDtoMapper, atLeast(1)).convert(anyList());
    }
}