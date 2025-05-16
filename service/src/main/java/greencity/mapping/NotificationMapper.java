package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationRequestDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationMapper extends AbstractConverter<NotificationRequestDto, Notification> {
    private final UserRepo userRepo;

    @Override
    public Notification convert(NotificationRequestDto dto) {
        User receiver = userRepo.findById(dto.getReceiverId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + dto.getReceiverId()));
        User initiator = userRepo.findById(dto.getInitiatorId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + dto.getReceiverId()));

        return Notification.builder()
            .action(dto.getAction())
            .objectName(dto.getObjectName())
            .objectLink(dto.getObjectLink())
            .creationDate(dto.getCreationDate())
            .status(dto.getStatus())
            .receiver(receiver)
            .initiator(initiator)
            .origin(dto.getOrigin())
            .build();
    }
}
