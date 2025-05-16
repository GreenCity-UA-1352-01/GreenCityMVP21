package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.*;
import greencity.mapping.NotificationMapper;
import greencity.notification.NotificationPublisher;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.localization.TagTranslation;
import greencity.enums.EventAttenderStatus;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.CancelledEventsRepository;
import greencity.repository.EventRepository;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import greencity.repository.EventLikeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepo userRepo;
    private final TagsRepo tagsRepo;
    private final EventRepository eventRepository;
    private final CancelledEventsRepository cancelledEventsRepository;
    private final FileService fileService;
    private final EventDateTimeLocationService eventDateTimeLocationService;
    private final EventLikeRepository eventLikeRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO) {
        User initiator = getUserById(userVO.getId());
        Event event = buildBaseEvent(dto, initiator);
        List<EventImage> eventImages = handleImages(images);
        event.setEventImages(eventImages);

        eventDateTimeLocationService.handleDateTimeLocations(dto.getDates(), event);
        updateEventTags(event, dto.getTags());

        Event savedEvent = eventRepository.save(event);

        return buildResponse(savedEvent);
    }

    @Override
    @Transactional
    public UpdateEventDtoResponse updateEvent(UpdateEventDtoRequest updateEventDtoRequest,
                                              List<MultipartFile> images, UserVO user) {
        Event event = getEventById(updateEventDtoRequest.getId());

        eventDateTimeLocationService.isFutureEvent(event.getDateTimes());

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(event.getInitiator().getId())) {
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        event.setTitle(updateEventDtoRequest.getTitle());
        event.setDescription(updateEventDtoRequest.getDescription());
        event.setOpen(updateEventDtoRequest.isOpen());
        eventDateTimeLocationService.updateEventDateTimeLocation(event,
                updateEventDtoRequest.getDateTimes());
        updateEventTags(event, updateEventDtoRequest.getTags());
        updateEventImages(event, images, updateEventDtoRequest);

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
        List<String> imagePaths = new ArrayList<>();
        if (event.getEventImages() != null) {
            for (EventImage image : event.getEventImages()) {
                imagePaths.add(image.getImagePath());
            }
        }
        return CreateEventDtoResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .open(event.isOpen())
                .tags(tagsConverter(event))
                .dates(eventDateTimeLocationService.getAllDates(event))
                .images(imagePaths)
                .createdDateTime(ZonedDateTime.now())
                .build();
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

    private void updateEventImages(Event event, List<MultipartFile> newImages, UpdateEventDtoRequest dto) {
        Set<String> keepOld = new HashSet<>();
        if (dto.getImages() != null) {
            dto.getImages().forEach(img -> keepOld.add(getFileNameFromUrl(img)));
        }

        String mainImageName = dto.getMainImage();
        int newImagesCount = (newImages != null) ? newImages.size() : 0;

        if ((keepOld.size() + newImagesCount) > 5) {
            throw new BadRequestException(ErrorMessage.MAX_PHOTO_EVENT);
        }

        validateMainImagePresence(keepOld, newImages, mainImageName);
        removeMissingImages(event, keepOld);

        Map<String, String> filenameToUploadedPath = new HashMap<>();
        if (newImages != null && !newImages.isEmpty()) {
            uploadNewImages(event, newImages, filenameToUploadedPath);
        }

        setMainImage(event, mainImageName, filenameToUploadedPath);
    }

    private void validateMainImagePresence(Set<String> keepOld, List<MultipartFile> newImages, String mainImageName) {
        boolean inOld = keepOld.stream()
                .anyMatch(old -> getFileNameFromUrl(old).endsWith(mainImageName));
        boolean inNew = newImages != null && newImages.stream()
                .anyMatch(f -> f != null && mainImageName.equals(f.getOriginalFilename()));

        if (!inOld && !inNew) {
            throw new BadRequestException(ErrorMessage.CANNOT_FOUND_MAIN_PHOTO_EVENT);
        }
    }

    private void removeMissingImages(Event event, Set<String> keepOld) {
        event.getEventImages().removeIf(img -> {
            String filename = getFileNameFromUrl(img.getImagePath());
            if (!keepOld.contains(filename)) {
                fileService.delete(img.getImagePath());
                return true;
            }
            return false;
        });
    }

    private void uploadNewImages(Event event, List<MultipartFile> newImages, Map<String, String> filenameToUploadedPath) {
        for (MultipartFile file : newImages) {
            String uploadedPath = fileService.upload(file);
            filenameToUploadedPath.put(file.getOriginalFilename(), uploadedPath);

            EventImage img = new EventImage();
            img.setImagePath(uploadedPath);
            img.setEvent(event);
            event.getEventImages().add(img);
        }
    }

    private void setMainImage(Event event, String mainImageName, Map<String, String> filenameToUploadedPath) {
        String uploadedPath = filenameToUploadedPath.get(mainImageName);
        if (uploadedPath != null) {
            event.setMainImage(
                    event.getEventImages().stream()
                            .filter(img -> img.getImagePath().equals(uploadedPath))
                            .findFirst()
                            .orElseThrow(() -> new BadRequestException(ErrorMessage.CANNOT_FOUND_MAIN_PHOTO_EVENT)));
            return;
        }

        event.setMainImage(
                event.getEventImages().stream()
                        .filter(img -> getFileNameFromUrl(img.getImagePath()).endsWith(mainImageName))
                        .findFirst()
                        .orElseThrow(() -> new BadRequestException(ErrorMessage.CANNOT_FOUND_MAIN_PHOTO_EVENT)));
    }

    private String getFileNameFromUrl(String urlOrPath) {
        if (urlOrPath == null || urlOrPath.isBlank()) return "";
        int slashIndex = urlOrPath.lastIndexOf('/');
        return slashIndex >= 0 ? urlOrPath.substring(slashIndex + 1) : urlOrPath;
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

    /**
     * {@inheritDoc}
     * Method check user is owner of event or has ADMIN role.
     * All images related to event will be deleted from external file storage.
     *
     * @param id   the ID of the event to be deleted
     * @param user the user requesting the deletion
     * @author Rostyslav Zadyraichuk
     */
    @Override
    @Transactional
    public void deleteById(Long id, UserVO user) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));

        if (!event.getInitiator().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventRepository.deleteById(id);

        if (event.getMainImage() != null) {
            fileService.delete(event.getMainImage().getImagePath());
        }
        event.getEventImages()
                .forEach(image -> fileService.delete(image.getImagePath()));
    }

    /**
     * {@inheritDoc}
     * Method for liking an event by its ID.
     * If the user has already liked this event, a {@link ConflictException} is thrown
     * and no notification is sent. This ensures idempotency and prevents duplicate notifications.
     *
     * @param id   the ID of the event to be liked
     * @param user the user who is liking the event
     * @throws ConflictException if the event was already liked by the user
     * @author Rostyslav Kushpit
     */
    @Override
    @Transactional
    public void likeEvent(Long id, UserVO user) {
        Event event = getEventById(id);
        if (eventLikeRepository.existsByEventIdAndUserId(id, user.getId())) {
            throw new ConflictException(ErrorMessage.EVENT_ALREADY_LIKED);
        }

        EventLike like = EventLike.builder()
                .event(event)
                .user(getUserById(user.getId()))
                .likedAt(ZonedDateTime.now())
                .build();
        eventLikeRepository.save(like);
    }

    /**
     * {@inheritDoc}
     * Method for unlike some event by its id.
     *
     * @param id   the ID of the event to be unliked
     * @param user the user who is unliking the event
     * @author Roman Diakov
     */
    @Override
    @Transactional
    public void unlikeEvent(Long id, UserVO user) {
        Event event = getEventById(id);
        if (eventLikeRepository.existsByEventIdAndUserId(id, user.getId())) {
            eventLikeRepository.deleteByEventIdAndUserId(id, user.getId());
            notificationService.deleteEventLikeNotification(user.getId(), event.getInitiator().getId(), id);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param id event id
     * @return {@link EventVO} with founded event
     * @throws NotFoundException if event not found
     * @author Rostyslav Zadyraichuk
     */
    @Override
    public EventVO findById(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        Event event = eventOpt.orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
        return modelMapper.map(event, EventVO.class);

    @Transactional
    @Override
    public void cancelEventById(Long id, UserVO user, String reason) {
        Event event = getEventById(id);
        User initiator = getUserById(user.getId());

        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(event.getInitiator().getId())) {
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventDateTimeLocationService.isFutureEvent(event.getDateTimes());

        checkIfEventNotCancelled(event);

        CancelledEvent cancelledEvent = CancelledEvent.builder()
                .event(event)
                .canceled_at(ZonedDateTime.now())
                .user(initiator)
                .reason(reason)
                .build();
        cancelledEventsRepository.save(cancelledEvent);
    }

    @Transactional
    @Override
    public void attendEvent(Long id, UserVO user) {
        Event event = getEventById(id);
        eventDateTimeLocationService.isFutureEvent(event.getDateTimes());
        checkIfEventNotCancelled(event);
        checkIfAttenderIsNotInitiator(event, user);
        checkIfAlreadyAttender(event, user.getId());
        checkIfAlreadyRequested(event, user.getId());
        EventAttender eventAttender = createEventAttender(event, user);

        if (!event.isOpen()) {
            eventAttender.setStatus(EventAttenderStatus.REQUESTED);
        }
        event.getAttenders().add(eventAttender);
        eventRepository.save(event);

    }

    @Transactional
    @Override
    public void acceptAttenderToEvent(Long eventId, Long userId, UserVO user) {
        Event event = getEventById(eventId);
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(event.getInitiator().getId())) {
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        eventDateTimeLocationService.isFutureEvent(event.getDateTimes());
        checkIfEventNotCancelled(event);
        checkIfAlreadyAttender(event, userId);
        EventAttender eventAttender = event.getAttenders().stream()
                .filter(attender -> attender.getAttender().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        eventAttender.setStatus(EventAttenderStatus.ACCEPTED);

    }

    private EventAttender createEventAttender(Event event, UserVO user) {
        return EventAttender.builder()
                .event(event)
                .attender(getUserById(user.getId()))
                .status(EventAttenderStatus.ACCEPTED)
                .build();
    }

    private void checkIfAlreadyAttender(Event event, Long userVOId) {
        event.getAttenders().stream()
                .filter(attender -> attender.getAttender().getId().equals(userVOId))
                .findFirst()
                .ifPresent(attender -> {
                    if (attender.getStatus() == EventAttenderStatus.ACCEPTED) {
                        throw new BadRequestException(ErrorMessage.USER_IS_ALREADY_ATTENDER);
                    }
                });
    }

    private void checkIfAlreadyRequested(Event event, Long userVOId) {
        event.getAttenders().stream()
                .filter(attender -> attender.getAttender().getId().equals(userVOId))
                .findFirst()
                .ifPresent(attender -> {
                    if (attender.getStatus() == EventAttenderStatus.REQUESTED) {
                        throw new BadRequestException(ErrorMessage.USER_IS_ALREADY_REQUESTED);
                    }
                });
    }


    private void checkIfAttenderIsNotInitiator(Event event, UserVO user) {
        if (event.getInitiator().getId().equals(getUserById(user.getId()).getId())) {
            throw new BadRequestException(ErrorMessage.USER_IS_INITIATOR);
        }
    }

    private void checkIfEventNotCancelled(Event event) {
        if (!cancelledEventsRepository.findByEventId(event.getId()).isEmpty()) {
            throw new BadRequestException(ErrorMessage.EVENT_ALREADY_CANCELLED);
        }
    }
    @Override
    public List<Long> findAttendersIdByEventId(Long eventId) {
        Event event = getEventById(eventId);
        return event.getAttenders().stream()
                .map(attender -> attender.getAttender().getId())
                .collect(Collectors.toList());
    }

    @Override
    public void unsubscribeFromEvent(Long id, UserVO userVO) {
        Event event = getEventById(id);
        eventDateTimeLocationService.isFutureEvent(event.getDateTimes());
        checkIfEventNotCancelled(event);
        User findUser = getUserById(userVO.getId());
        if (!event.getAttenders().removeIf(attender -> attender.getAttender().getId().equals(findUser.getId()))) {
            throw new NotFoundException(ErrorMessage.USER_IS_NOT_ATTENDER);
        }
        eventRepository.save(event);
    }
}
