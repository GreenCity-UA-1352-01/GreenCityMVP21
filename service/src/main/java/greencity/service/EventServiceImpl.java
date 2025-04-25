package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;

    @Override
    public UpdateEventDtoResponse findById(Long id) {
        Event event = getById(id);
        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }

    @Override
    @Transactional
    public UpdateEventDtoResponse update(UpdateEventDtoRequest updateEventDtoRequest,
                                         List<MultipartFile> images, UserVO user) {
        Event event = getById(updateEventDtoRequest.getId());

        boolean hasFutureEvent = event.getDateTimes().stream()
                .anyMatch(dateTime -> dateTime.getStartDateTime().isAfter(ZonedDateTime.now()));

        if (!hasFutureEvent) {
            throw new BadRequestException(ErrorMessage.CANNOT_EDIT_PAST_EVENT);
        }

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(event.getInitiator().getId())){
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        // додати логіку оновлення полів
        eventRepository.save(event);
        return  modelMapper.map(event,UpdateEventDtoResponse.class);
    }

    private Event getById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
    }

}
