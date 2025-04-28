package greencity.service;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FileService fileService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private User user;
    private UserVO userVO;
    private Event event;

    @BeforeEach
    void setUp() {
        user = ModelUtils.getUser();
        userVO = ModelUtils.getUserVO();
        event = ModelUtils.getEvent();
        event.setInitiator(user);
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
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));
        userVO.setId(userVO.getId() + 1);

        assertThrows(UserHasNoPermissionToAccessException.class, () ->
            eventService.deleteById(event.getId(), userVO));

        verify(eventRepository).findById(event.getId());
        verify(eventRepository, never()).deleteById(event.getId());
    }

    @Test
    void testDeleteById_withOwner() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));

        assertDoesNotThrow(() -> eventService.deleteById(event.getId(), userVO));

        verify(eventRepository).findById(event.getId());
        verify(eventRepository).deleteById(event.getId());
    }

    @Test
    void testDeleteById_withAdmin() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));
        userVO.setId(userVO.getId() + 1);
        userVO.setRole(Role.ROLE_ADMIN);

        assertDoesNotThrow(() -> eventService.deleteById(event.getId(), userVO));

        verify(eventRepository).findById(event.getId());
        verify(eventRepository).deleteById(event.getId());
    }

    @Test
    void testDeleteById_withPictures() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));
        doNothing().when(fileService).delete(anyString());
        EventImage eventImage = EventImage.builder()
            .id(1L)
            .event(event)
            .imagePath("test")
            .build();
        event.setMainImage(eventImage);
        event.getEventImages().add(eventImage);

        eventService.deleteById(event.getId(), userVO);

        verify(fileService, atLeast(1)).delete(anyString());
    }

    @Test
    void testDeleteById_withoutPictures() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.ofNullable(event));

        eventService.deleteById(event.getId(), userVO);

        verify(fileService, never()).delete(anyString());
    }
}