package greencity.service;

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

    @Mock
    private NotificationRepo notificationRepo;

    @Mock
    private NotificationCounterRepo notificationCounterRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

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