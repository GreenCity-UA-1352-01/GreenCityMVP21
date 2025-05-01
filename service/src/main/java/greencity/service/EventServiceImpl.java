package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.EventRepository;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepo userRepo;
    private final TagsRepo tagsRepo;
    private final EventRepository eventRepository;
    private final FileService fileService;
    private final EventDateTimeLocationService eventDateTimeLocationService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO) {
        User initiator = getUserById(userVO.getId());
        Event event = buildBaseEvent(dto, initiator);
        List<EventImage> eventImages = handleImages(images);
        event.setEventImages(eventImages);

        eventDateTimeLocationService.handleDateTimeLocations(dto.getDates(), event);
        handleTags(dto.getTags(), event);

        Event savedEvent = eventRepository.save(event);

        return buildResponse(savedEvent);
    }

    @Override
    @Transactional
    public UpdateEventDtoResponse updateEvent(UpdateEventDtoRequest updateEventDtoRequest,
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
        eventDateTimeLocationService.updateEventDateTimeLocation(event,
                updateEventDtoRequest.getDateTimes());

        updateEventTags(event, updateEventDtoRequest.getTags());
        updateEventImages(event, images, updateEventDtoRequest.getMainImage());

        eventRepository.save(event);

        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }
    
    private List<String> tagsConverter(Event event) {
        return event.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName)
                .collect(Collectors.toList());
    }

    private Event buildBaseEvent(CreateEventDto dto, User initiator) {
        Event event = modelMapper.map(dto, Event.class);
        event.setInitiator(initiator);
        return event;
    }

    private CreateEventDtoResponse buildResponse(Event event) {
        return CreateEventDtoResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .open(event.isOpen())
                .tags(tagsConverter(event))
                .dates(eventDateTimeLocationService.getAllDates(event))
                .createdDateTime(ZonedDateTime.now())
                .build();
    }

private void handleTags(List<TagUaEnDto> tagsDto, Event event) {
    if (tagsDto != null) {
        Set<Tag> tags = tagsDto.stream()
                .map(tagDto -> {
                    Tag tag = tagsRepo.findById(tagDto.getId())
                            .orElseThrow(() -> new TagNotFoundException(ErrorMessage.TAG_NOT_FOUND));
                    if (tag.getType() != TagType.EVENT) {
                        throw new BadRequestException(ErrorMessage.INVALID_TAG_TYPE + " Should be " + TagType.EVENT);
                    }
                    return tag;
                })
                .collect(Collectors.toSet());
        event.setTags(tags);
    }
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

    private void updateEventImages(Event event, List<MultipartFile> images, String mainImageFilename) {
        MultipartFile mainImageFile = images.stream()
                .filter(file -> file.getOriginalFilename() != null && file.getOriginalFilename().equals(mainImageFilename))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorMessage.CANNOT_FOUND_MAIN_PHOTO_EVENT));

        event.getEventImages().forEach(img -> {
            fileService.delete(img.getImagePath());
        });
        event.getEventImages().clear();

        List<EventImage> eventImages = new ArrayList<>();

        for (MultipartFile file : images) {
            EventImage eventImage = new EventImage();
            String uploadedPath = fileService.upload(file);
            eventImage.setImagePath(uploadedPath);
            eventImage.setEvent(event);
            eventImages.add(eventImage);
        }

        EventImage mainImage = eventImages.stream()
                .filter(img -> img.getImagePath().endsWith(mainImageFile.getOriginalFilename()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorMessage.CANNOT_FOUND_MAIN_PHOTO_EVENT));

        event.getEventImages().addAll(eventImages);
        event.setMainImage(mainImage);
    }

    @Override
    public UpdateEventDtoResponse getUpdateEventDto(Long id) {
        Event event = getEventById(id);
        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }

    private Event getEventById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
    }

    private User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}
