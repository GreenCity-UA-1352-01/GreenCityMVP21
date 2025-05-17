package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.entity.NotificationCounter;
import greencity.entity.User;
import org.modelmapper.ModelMapper;
import greencity.mapping.NotificationMapper;
import greencity.mapping.NotificationResponseDtoMapper;
import greencity.repository.NotificationCounterRepo;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationsRepo;
    private final NotificationCounterRepo notificationCounterRepo;
    private final NotificationMapper notificationMapper;
    private final NotificationResponseDtoMapper notificationResponseDtoMapper;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto dto) {
        Notification notification = notificationMapper.convert(dto);
        notification = notificationsRepo.save(notification);
        final User receiver = notification.getReceiver();

        notificationCounterRepo.findById(dto.getReceiverId()).ifPresentOrElse(notificationCounter ->
                notificationCounter.setCountOfNotifications(notificationCounter.getCountOfNotifications() + 1),
            () -> notificationCounterRepo.save(NotificationCounter.builder()
                .countOfNotifications(1)
                .user(receiver)
                .build())
        );
        return notificationResponseDtoMapper.convert(notification);
    }

    @Override
    public List<NotificationResponseDto> getAllNotificationsForUser(Long userId) {
        return notificationsRepo.findAllByReceiverIdOrderByCreationDateDesc(userId).stream()
                .map(notification -> {
                    NotificationResponseDto dto = modelMapper.map(notification, NotificationResponseDto.class);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}