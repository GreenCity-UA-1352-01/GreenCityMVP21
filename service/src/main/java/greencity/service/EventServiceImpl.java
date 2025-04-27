package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventDateTimeLocation;
import greencity.entity.Tag;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.EventRepository;
import greencity.repository.TagsRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;
    private final TagsRepo tagsRepo;
    private final TagsServiceImpl tagService;

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
        event.setOpen(updateEventDtoRequest.isOpen());
        updateEventDateTimeLocation(event, updateEventDtoRequest.getDateTimes());

        updateEventTags(event, updateEventDtoRequest.getTags());

        eventRepository.save(event);

        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }

    private Event getEventById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
    }

    private void updateEventDateTimeLocation(Event event, List<EventDateTimeLocationRequestDto> dtoList) {
        List<EventDateTimeLocation> updatedDates = dtoList.stream()
                .map(dateTimeDto -> {
                    EventDateTimeLocation dateTimeLocation = event.getDateTimes().stream()
                            .filter(dt -> dt.getId() != null && dt.getId().equals(dateTimeDto.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                EventDateTimeLocation newDateTime = new EventDateTimeLocation();
                                newDateTime.setEvent(event);
                                return newDateTime;
                            });
                    dateTimeLocation.setStartDateTime(dateTimeDto.getStartDateTime());
                    dateTimeLocation.setEndDateTime(dateTimeDto.getEndDateTime());
                    dateTimeLocation.setLocation(dateTimeDto.getLocation());
                    dateTimeLocation.setLink(dateTimeDto.getLink());
                    return dateTimeLocation;
                })
                .toList();
        event.getDateTimes().clear();
        event.getDateTimes().addAll(updatedDates);
    }


    private void updateEventTags(Event event, List<String> tags) {
        Set<Tag> updateTags = new HashSet<>(tagsRepo.findTagsByNamesAndType(
                tags.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList()),
                TagType.EVENT
        ));
        if (updateTags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        event.setTags(updateTags);
    }

}
