package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.dto.eventimage.EventImageResponseDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private TagsRepo tagsRepo;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private FileService fileService;
    @Mock
    private EventDateTimeLocationService eventDateTimeLocationService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    CancelledEventsRepository cancelledEventsRepository;


    @InjectMocks
    private EventServiceImpl eventService;

    private CreateEventDto createEventDto;
    private CreateEventDtoResponse expectedResponse;
    private User user;
    private UserVO userVO;
    private Event event;

    @BeforeEach
    void setUp() {
        createEventDto = ModelUtils.getCreateEventDto();
        expectedResponse = ModelUtils.getCreateEventDtoResponse();
        user = ModelUtils.getUser();
        userVO = ModelUtils.getUserVO();
        event = ModelUtils.getEvent();
        event.setInitiator(user);
    }

    @Test
    void createEvent_ReturnsCreateEventDtoResponse() {
        List<MultipartFile> images = Collections.emptyList();

        Tag tag = ModelUtils.getEventTag();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(CreateEventDto.class), eq(Event.class))).thenReturn(event);
        when(tagsRepo.findTagsByNamesAndType(anyList(), eq(TagType.EVENT))).thenReturn(List.of(tag));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventDateTimeLocationService.getAllDates(event)).thenReturn(List.of(ModelUtils.getEventDateLocationDto()));

        CreateEventDtoResponse actualResponse = eventService.createEvent(createEventDto, images, userVO);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getTags(), actualResponse.getTags());
        assertEquals(expectedResponse.getOpen(), actualResponse.getOpen());
        assertEquals(expectedResponse.getDates(), actualResponse.getDates());

        verify(userRepo).findById(user.getId());
        verify(modelMapper).map(any(CreateEventDto.class), eq(Event.class));
        verify(tagsRepo).findTagsByNamesAndType(anyList(), eq(TagType.EVENT));
        verify(eventRepository).save(event);
        verify(eventDateTimeLocationService).handleDateTimeLocations(createEventDto.getDates(), event);
        verify(eventDateTimeLocationService).getAllDates(event);
        verifyNoInteractions(fileService);
    }

    @Test
    void createEvent_WithImages_ReturnsCreateEventDtoResponse() {
        List<MultipartFile> images = new ArrayList<>();
        MultipartFile image = mock(MultipartFile.class);
        when(image.getOriginalFilename()).thenReturn("image1.jpg");
        when(fileService.upload(image)).thenReturn("path/to/image1.jpg");
        images.add(image);

        Tag tag = ModelUtils.getEventTag();
        Event event = ModelUtils.getEvent();
        event.setTags(Set.of(tag));

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(CreateEventDto.class), eq(Event.class))).thenReturn(event);
        when(tagsRepo.findTagsByNamesAndType(anyList(), eq(TagType.EVENT))).thenReturn(List.of(tag));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventDateTimeLocationService.getAllDates(event)).thenReturn(List.of(ModelUtils.getEventDateLocationDto()));

        CreateEventDtoResponse response = eventService.createEvent(createEventDto, images, userVO);

        assertNotNull(response);
        assertEquals(createEventDto.getTitle(), response.getTitle());
        assertEquals(createEventDto.getDescription(), response.getDescription());
        assertEquals(createEventDto.getOpen(), response.getOpen());
        assertEquals(1, response.getImages().size());
        assertEquals("path/to/image1.jpg", response.getImages().getFirst());
    }

    @Test
    void createEvent_TagsNotFound_ThrowsException() {
        List<MultipartFile> images = Collections.emptyList();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(CreateEventDto.class), eq(Event.class))).thenReturn(event);
        when(tagsRepo.findTagsByNamesAndType(anyList(), eq(TagType.EVENT))).thenReturn(Collections.emptyList());

        assertThrows(TagNotFoundException.class,
                () -> eventService.createEvent(createEventDto, images, userVO));

        verify(userRepo).findById(user.getId());
        verify(tagsRepo).findTagsByNamesAndType(anyList(), eq(TagType.EVENT));
    }

    @Test
    void testDeleteById_withNotExistedUserId_shouldThrowException() {
        when(eventRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () ->
                eventService.deleteById(event.getId(), userVO));

        verify(eventRepository).findById(event.getId());
        verify(eventRepository, never()).deleteById(event.getId());
    }

    @Test
    void testDeleteById_withNotOwner_shouldThrowException() {
        Event actual = ModelUtils.getEventWithoutImages();

        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(actual));
        userVO.setId(userVO.getId() + 1);

        assertThrows(UserHasNoPermissionToAccessException.class, () ->
                eventService.deleteById(actual.getId(), userVO));

        verify(eventRepository).findById(actual.getId());
        verify(eventRepository, never()).deleteById(actual.getId());
    }

    @Test
    void testDeleteById_withOwner() {
        Event actual = ModelUtils.getEventWithoutImages();

        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(actual));

        assertDoesNotThrow(() -> eventService.deleteById(actual.getId(), userVO));

        verify(eventRepository).findById(actual.getId());
        verify(eventRepository).deleteById(actual.getId());
    }

    @Test
    void testDeleteById_withAdmin() {
        Event actual = ModelUtils.getEventWithoutImages();

        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(actual));
        userVO.setId(userVO.getId() + 1);
        userVO.setRole(Role.ROLE_ADMIN);

        assertDoesNotThrow(() -> eventService.deleteById(actual.getId(), userVO));

        verify(eventRepository).findById(actual.getId());
        verify(eventRepository).deleteById(actual.getId());
    }

    @Test
    void testDeleteById_withPictures() {
        Event actual = ModelUtils.getEventWithoutImages();
        EventImage eventImage = EventImage.builder()
                .id(1L)
                .event(actual)
                .imagePath("test")
                .build();
        actual.setMainImage(eventImage);
        actual.getEventImages().add(eventImage);

        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(actual));
        doNothing().when(fileService).delete(anyString());

        eventService.deleteById(actual.getId(), userVO);

        verify(fileService, atLeast(1)).delete(anyString());
    }

    @Test
    void testDeleteById_withoutPictures() {
        Event actual = ModelUtils.getEventWithoutImages();

        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(actual));

        eventService.deleteById(actual.getId(), userVO);

        verify(fileService, never()).delete(anyString());
    }

    @Test
    void updateEvent_Success_WithoutNewImages() {
        ModelMapper modelMapper2 = new ModelMapper();
        EventServiceImpl eventService2 = new EventServiceImpl(
                userRepo,
                tagsRepo,
                eventRepository,
                cancelledEventsRepository,
                fileService,
                eventDateTimeLocationService,
                modelMapper2
        );

        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        UserVO user = ModelUtils.getUserVO();
        Event event = ModelUtils.getEvent();
        event.setId(request.getId());
        event.getDateTimes().getFirst().setId(1L);

        request.setMainImage("second.jpg");
        request.setImages(List.of("https://cdn.com/file/second.jpg"));

        event.setEventImages(new ArrayList<>(List.of(
                EventImage.builder().imagePath("https://cdn.com/file/second.jpg").build()
        )));
        event.setMainImage(EventImage.builder().imagePath("second.jpg").build());

        Set<Tag> tagSet = Set.of(ModelUtils.getEventTag());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagsRepo.findTagsByNamesAndType(any(), any())).thenReturn(new ArrayList<>(tagSet));
        doAnswer(invocation -> {
            Event evt = invocation.getArgument(0);
            List<EventDateTimeLocationRequestDto> newDates = invocation.getArgument(1);

            List<EventDateTimeLocation> updatedDates = newDates.stream()
                    .map(dto -> EventDateTimeLocation.builder()
                            .startDateTime(dto.getStartDateTime())
                            .endDateTime(dto.getEndDateTime())
                            .location(dto.getLocation())
                            .link(dto.getLink())
                            .build())
                    .toList();

            evt.setDateTimes(updatedDates);
            return null;
        }).when(eventDateTimeLocationService).updateEventDateTimeLocation(any(), any());
        UpdateEventDtoResponse result = eventService2.updateEvent(request, null, user);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Update Title", result.getTitle());
        assertEquals("description with more than 20 characters", result.getDescription());
        assertTrue(result.isOpen());
        assertNotNull(result.getMainImage());
        assertEquals("https://cdn.com/file/second.jpg", result.getMainImage().getImagePath());
        assertEquals(1, result.getEventImages().size());
        assertEquals("https://cdn.com/file/second.jpg", result.getEventImages().getFirst().getImagePath());
        assertEquals(1, result.getDateTimes().size());
        assertEquals("Update location", result.getDateTimes().getFirst().getLocation());
        assertEquals("Update link", result.getDateTimes().getFirst().getLink());
        assertEquals(ZonedDateTime.parse("2026-12-14T10:30Z"), result.getDateTimes().getFirst().getStartDateTime());
        assertEquals(ZonedDateTime.parse("2026-12-15T12:28Z"), result.getDateTimes().getFirst().getEndDateTime());
        assertEquals(1, result.getTags().size());
        TagVO tag = result.getTags().iterator().next();
        assertEquals("Соціальний", tag.getTagTranslations().getFirst().getName());

        verify(eventDateTimeLocationService).isFutureEvent(any());
        verify(eventDateTimeLocationService).updateEventDateTimeLocation(eq(event), any());
        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_Success_WithNewImages() {
        ModelMapper modelMapper2 = new ModelMapper();
        EventServiceImpl eventService2 = new EventServiceImpl(
                userRepo,
                tagsRepo,
                eventRepository,
                cancelledEventsRepository,
                fileService,
                eventDateTimeLocationService,
                modelMapper2
        );

        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        UserVO user = ModelUtils.getUserVO();
        Event event = ModelUtils.getEvent();
        event.setId(request.getId());
        event.getDateTimes().getFirst().setId(1L);


        request.setImages(List.of(""));


        Set<Tag> tagSet = Set.of(ModelUtils.getEventTag());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagsRepo.findTagsByNamesAndType(any(), any())).thenReturn(new ArrayList<>(tagSet));


        MultipartFile mockFile = new MockMultipartFile(
                "images",
                "UpdateMain.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy content".getBytes()
        );

        when(fileService.upload(mockFile)).thenReturn("https://cdn.com/file/UpdateMain.jpg");

        UpdateEventDtoResponse result = eventService2.updateEvent(request, List.of(mockFile), user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getMainImage());
        assertEquals("https://cdn.com/file/UpdateMain.jpg", result.getMainImage().getImagePath());
        assertEquals(1, result.getEventImages().size());
        assertEquals("https://cdn.com/file/UpdateMain.jpg", result.getEventImages().get(0).getImagePath());
        assertEquals(1, result.getTags().size());
        TagVO tag = result.getTags().iterator().next();
        assertEquals("Соціальний", tag.getTagTranslations().getFirst().getName());

        verify(eventDateTimeLocationService).isFutureEvent(any());
        verify(eventDateTimeLocationService).updateEventDateTimeLocation(eq(event), any());
        verify(fileService).upload(mockFile);
        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_ThrowsBadRequest_WhenTooManyImages() {
        ModelMapper modelMapper2 = new ModelMapper();
        EventServiceImpl eventService2 = new EventServiceImpl(
                userRepo,
                tagsRepo,
                eventRepository,
                cancelledEventsRepository,
                fileService,
                eventDateTimeLocationService,
                modelMapper2
        );
        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        UserVO user = ModelUtils.getUserVO();
        Event event = ModelUtils.getEvent();
        event.setId(request.getId());

        request.setImages(List.of(
                "https://cdn.com/file/old1.jpg",
                "https://cdn.com/file/old2.jpg",
                "https://cdn.com/file/old3.jpg",
                "https://cdn.com/file/old4.jpg"
        ));

        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("images", "new1.jpg", MediaType.IMAGE_JPEG_VALUE, "data".getBytes()),
                new MockMultipartFile("images", "new2.jpg", MediaType.IMAGE_JPEG_VALUE, "data".getBytes())
        );
        Set<Tag> tagSet = Set.of(ModelUtils.getEventTag());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagsRepo.findTagsByNamesAndType(any(), any())).thenReturn(new ArrayList<>(tagSet));

        assertThrows(BadRequestException.class,
                () -> eventService2.updateEvent(request, newImages, user));

        verify(eventRepository).findById(1L);
    }

    @Test
    void updateEvent_ThrowsBadRequest_WhenMainImageMissing() {
        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        request.setMainImage("nonexistent.jpg");

        request.setImages(List.of("https://cdn.com/file/old1.jpg"));

        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("images", "new1.jpg", MediaType.IMAGE_JPEG_VALUE, "data".getBytes())
        );

        UserVO user = ModelUtils.getUserVO();
        Event event = ModelUtils.getEvent();
        event.setId(request.getId());
        Set<Tag> tagSet = Set.of(ModelUtils.getEventTag());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagsRepo.findTagsByNamesAndType(any(), any())).thenReturn(new ArrayList<>(tagSet));

        assertThrows(BadRequestException.class,
                () -> eventService.updateEvent(request, newImages, user));
    }

    @Test
    void updateEvent_ThrowsBadRequest_WhenImageFormatInvalid() {
        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        request.setMainImage("invalid.gif");
        request.setImages(List.of());

        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("images", "invalid.gif", "image/gif", "data".getBytes())
        );

        UserVO user = ModelUtils.getUserVO();
        Event event = ModelUtils.getEvent();
        event.setId(request.getId());
        Set<Tag> tagSet = Set.of(ModelUtils.getEventTag());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagsRepo.findTagsByNamesAndType(any(), any())).thenReturn(new ArrayList<>(tagSet));

        assertThrows(BadRequestException.class,
                () -> eventService.updateEvent(request, newImages, user));
    }

    @Test
    void updateEvent_ShouldRemoveImages_ThatAreMissingInRequest() {
        ModelMapper modelMapper2 = new ModelMapper();
        EventServiceImpl service = new EventServiceImpl(
                userRepo, tagsRepo, eventRepository, cancelledEventsRepository, fileService, eventDateTimeLocationService, modelMapper2
        );
        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        request.setImages(List.of(
                "https://cdn.com/file/img1.jpg",
                "https://cdn.com/file/img3.jpg",
                "https://cdn.com/file/img5.jpg"
        ));
        request.setMainImage("img1.jpg");

        UserVO user = ModelUtils.getUserVO();
        Event event = ModelUtils.getEvent();
        event.setId(request.getId());

        List<EventImage> originalImages = List.of(
                EventImage.builder().imagePath("https://cdn.com/file/img1.jpg").build(),
                EventImage.builder().imagePath("https://cdn.com/file/img2.jpg").build(),
                EventImage.builder().imagePath("https://cdn.com/file/img3.jpg").build(),
                EventImage.builder().imagePath("https://cdn.com/file/img4.jpg").build(),
                EventImage.builder().imagePath("https://cdn.com/file/img5.jpg").build()
        );
        event.setEventImages(new ArrayList<>(originalImages));

        Set<Tag> tagSet = Set.of(ModelUtils.getEventTag());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(tagsRepo.findTagsByNamesAndType(any(), any())).thenReturn(new ArrayList<>(tagSet));


        UpdateEventDtoResponse result = service.updateEvent(request, null, user);

        assertEquals(3, result.getEventImages().size());
        List<String> expectedPaths = List.of(
                "https://cdn.com/file/img1.jpg",
                "https://cdn.com/file/img3.jpg",
                "https://cdn.com/file/img5.jpg"
        );
        List<String> actualPaths = result.getEventImages().stream()
                .map(EventImageResponseDto::getImagePath)
                .toList();

        assertTrue(actualPaths.containsAll(expectedPaths));
        assertFalse(actualPaths.contains("https://cdn.com/file/img2.jpg"));
        assertFalse(actualPaths.contains("https://cdn.com/file/img4.jpg"));
    }

    @Test
    void attendEvent_Success_OpenEvent() {
        Event event = ModelUtils.getEvent();
        UserVO userVO = ModelUtils.getUserVO();
        event.getInitiator().setId(2L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(user));

        eventService.attendEvent(1L, userVO);

        assertEquals(1, event.getAttenders().size());
        assertEquals(EventAttenderStatus.ACCEPTED, event.getAttenders().getFirst().getStatus());
    }

    @Test
    void attendEvent_UserIsInitiator_ThrowsException() {
        Event event = ModelUtils.getEvent();
        UserVO userVO = ModelUtils.getUserVO();

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.attendEvent(1L, userVO));
    }

    @Test
    void attendEvent_AlreadyAccepted_ThrowsException() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(3L);

        event.getAttenders().add(EventAttender.builder()
                .attender(user)
                .event(event)
                .status(EventAttenderStatus.ACCEPTED)
                .build());

        UserVO userVO = ModelUtils.getUserVO();
        userVO.setId(3L);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.attendEvent(1L, userVO));
    }

    @Test
    void attendEvent_AlreadyRequested_ThrowsException() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(4L);

        event.getAttenders().add(EventAttender.builder()
                .attender(user)
                .event(event)
                .status(EventAttenderStatus.REQUESTED)
                .build());

        UserVO userVO = ModelUtils.getUserVO();
        userVO.setId(4L);

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.attendEvent(1L, userVO));
    }

    @Test
    void attendEvent_ClosedEvent_StatusRequested() {
        Event event = ModelUtils.getEvent();
        event.setOpen(false);

        User user = ModelUtils.getUser();
        user.setId(3L);

        UserVO userVO = ModelUtils.getUserVO();
        userVO.setId(3L);


        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(userRepo.findById(userVO.getId())).thenReturn(Optional.of(user));


        eventService.attendEvent(3L, userVO);


        assertEquals(1, event.getAttenders().size());
        assertEquals(EventAttenderStatus.REQUESTED, event.getAttenders().get(0).getStatus());
    }

    @Test
    void acceptAttenderToEvent_Success() {
        Event event = ModelUtils.getEvent();
        User initiator = ModelUtils.getUser();
        initiator.setId(1L);
        event.setInitiator(initiator);

        UserVO userVO = ModelUtils.getUserVO(); // инициатор
        userVO.setId(1L);
        userVO.setRole(Role.ROLE_USER);

        User attenderUser = new User();
        attenderUser.setId(2L);

        EventAttender attender = new EventAttender();
        attender.setAttender(attenderUser);
        attender.setStatus(EventAttenderStatus.REQUESTED);
        event.setAttenders(new ArrayList<>(List.of(attender)));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.acceptAttenderToEvent(1L, 2L, userVO);

        assertEquals(EventAttenderStatus.ACCEPTED, event.getAttenders().get(0).getStatus());
    }

    @Test
    void acceptAttenderToEvent_AccessDenied_UserIsNotAdminOrInitiator() {
        User initiator = new User();
        initiator.setId(1L);

        Event event = ModelUtils.getEvent();
        event.setInitiator(initiator);
        event.setAttenders(new ArrayList<>());

        UserVO notAllowedUser = new UserVO();
        notAllowedUser.setId(2L);
        notAllowedUser.setRole(Role.ROLE_USER); // не админ

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(AccessDeniedException.class, () ->
                eventService.acceptAttenderToEvent(1L, 3L, notAllowedUser)
        );
    }


}

