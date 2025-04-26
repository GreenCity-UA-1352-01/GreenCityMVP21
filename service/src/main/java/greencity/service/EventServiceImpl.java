package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventDateTimeLocation;
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
    public UpdateEventDtoResponse findUpdateEventDtoResponseById(Long id) {
        Event event = getEventById(id);
        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }

    @Override
    @Transactional
    public UpdateEventDtoResponse update(UpdateEventDtoRequest updateEventDtoRequest,
                                         List<MultipartFile> images, UserVO user) {
        Event event = getEventById(updateEventDtoRequest.getId());
        boolean hasFutureEvent = event.getDateTimes().stream()
                .anyMatch(dateTime -> dateTime.getStartDateTime().isAfter(ZonedDateTime.now()));

        if (!hasFutureEvent) {
            throw new BadRequestException(ErrorMessage.CANNOT_EDIT_PAST_EVENT);
        }

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(event.getInitiator().getId())) {
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }


        event.setTitle(updateEventDtoRequest.getTitle());
        event.setDescription(updateEventDtoRequest.getDescription());

        updateEventDateTimeLocation(event, updateEventDtoRequest.getDateTimes());

        eventRepository.save(event);
        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }

    private Event getEventById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
    }

    private void updateEventDateTimeLocation(Event event, List<EventDateTimeLocationRequestDto> dto) {
        List<EventDateTimeLocation> newDates = dto.stream()
                .map(dateTimeDto -> {
                    EventDateTimeLocation dt = modelMapper.map(dateTimeDto, EventDateTimeLocation.class);
                    dt.setEvent(event);
                    return dt;
                })
                .toList();
        event.getDateTimes().clear();
        event.getDateTimes().addAll(newDates);
    }
}
