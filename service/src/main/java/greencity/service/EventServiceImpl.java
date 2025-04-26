package greencity.service;

import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import greencity.mapping.EventDateLocationDtoMapper;
import greencity.repository.EventRepository;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final TagsRepo tagsRepo;
    private final EventRepository eventRepository;
    private final FileService fileService;
    private final EventDateLocationDtoMapper eventDateLocationDtoMapper;


    @Override
    @Transactional
    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO) {
        User initiator = getUserById(userVO.getId());
        Event event = buildBaseEvent(dto, initiator);
        List<EventImage> eventImages = handleImages(images);
        event.setEventImages(eventImages);
        handleDateTimeLocations(dto.getDates(), event);
        handleTags(dto.getTags(), event);

        Event savedEvent = eventRepository.save(event);
        return buildResponse(savedEvent);
    }

    private User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private Event buildBaseEvent(CreateEventDto dto, User initiator) {
        Event event = modelMapper.map(dto, Event.class);
        event.setInitiator(initiator);
        return event;
    }

    private List<EventImage> handleImages(List<MultipartFile> files) {
        List<EventImage> eventImages = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return eventImages;
        } else {
            for (MultipartFile file : files) {
                String imagePath = fileService.upload(file);
                eventImages.add(EventImage.builder()
                        .imagePath(imagePath)
                        .build());
            }
            return eventImages;
        }
    }

    private void handleDateTimeLocations(List<EventDateLocationDto> dates, Event event) {
        List<EventDateTimeLocation> dateTimes = dates.stream()
                .map(dto -> EventDateTimeLocation.builder()
                        .startDateTime(dto.getStartDateTime())
                        .endDateTime(dto.getEndDateTime())
                        .location(dto.getLocation())
                        .link(dto.getOnlineLink())
                        .event(event)
                        .build())
                .collect(Collectors.toList());

        event.setDateTimes(dateTimes);
    }

    private void handleTags(List<TagUaEnDto> tagsDto, Event event) {
        if (tagsDto != null) {
            Set<Tag> tags = tagsDto.stream()
                    .map(tagDto -> tagsRepo.findById(tagDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + tagDto.getId())))
                    .collect(Collectors.toSet());
            event.setTags(tags);
        }
    }


    private List<String> tagsConverter (Event event) {
        return event.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName)
                .collect(Collectors.toList());
    }
    private List<EventDateLocationDto> getAllDates(Event event) {
        return event.getDateTimes().stream()
                .map(eventDateLocationDtoMapper::convert)
                .collect(Collectors.toList());
    }

    private CreateEventDtoResponse buildResponse(Event event) {
        return CreateEventDtoResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .open(event.isOpen())
                .tags(tagsConverter(event))
                .dates(getAllDates(event))
                .createdDateTime(ZonedDateTime.now())
                .build();
    }


}


