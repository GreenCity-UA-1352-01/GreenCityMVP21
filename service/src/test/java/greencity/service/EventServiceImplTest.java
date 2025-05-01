package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.EventImage;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepository;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

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

}

