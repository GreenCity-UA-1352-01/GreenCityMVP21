package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.entity.Event;
import greencity.entity.EventDateTimeLocation;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventDateTimeLocationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.getEventDateTimeLocationRequestDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventDateTimeLocationServiceTest {
    private final ZonedDateTime FIXED_EVENT_START = ModelUtils.FIXED_EVENT_START;
    private final ZonedDateTime FIXED_EVENT_END = ModelUtils.FIXED_EVENT_END;

    @Mock
    private EventDateTimeLocationRepo evDateTimeLocRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventDateTimeLocationService eventDateTimeLocationService;

    private Event event;
    private List<EventDateTimeLocationRequestDto> dtoList;
    private List<EventDateTimeLocation> eventDateTimeLocations;

    @BeforeEach
    void setUp() {
        event = ModelUtils.getEvent();
        event.getDateTimes().getFirst().setId(1L);
        dtoList = new ArrayList<>();
        dtoList.add(getEventDateTimeLocationRequestDto());
        eventDateTimeLocations = new ArrayList<>();
        eventDateTimeLocations.add(event.getDateTimes().getFirst());
        System.out.println(event.getDateTimes());
    }

    @Test
    void testCheckStartEndDates_Success() {
        // Given valid list of start and end dates - our dtoList after setUp() method;

        // When
        assertDoesNotThrow(() -> eventDateTimeLocationService.checkStartEndDates(dtoList));
    }

    @Test
    void testStartEndDateTimeCheck_ThrowsException_WhenCheckStartDateAfterEndDate() {
        // Given invalid start and end dates (start after end)
        dtoList.getFirst().setStartDateTime(FIXED_EVENT_END);
        dtoList.getFirst().setEndDateTime(FIXED_EVENT_START);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> eventDateTimeLocationService.checkStartEndDates(dtoList));
        assertEquals(ErrorMessage.START_DATE_TIME_AFTER_END_DATE_TIME, exception.getMessage());
    }

    @Test
    void testStartEndDateTimeCheck_ThrowsException_WhenCheckStartDateEqualsEndDate() {
        // Given invalid start and end dates (equal dates)
        dtoList.getFirst().setStartDateTime(FIXED_EVENT_START);
        dtoList.getFirst().setEndDateTime(FIXED_EVENT_START);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> eventDateTimeLocationService.checkStartEndDates(dtoList));
        assertEquals(ErrorMessage.START_DATE_TIME_AFTER_END_DATE_TIME, exception.getMessage());
    }

    @Test
    void testIsFutureEvent_Success() {
        // Given a list with at least one future event
        eventDateTimeLocations.getFirst().setStartDateTime(FIXED_EVENT_START);
        eventDateTimeLocations.getFirst().setEndDateTime(FIXED_EVENT_END);

        // When & Then
        assertDoesNotThrow(() -> eventDateTimeLocationService.isFutureEvent(eventDateTimeLocations));
    }

    @Test
    void testIsFutureEvent_ThrowsException_WhenNoFutureEvents() {
        // Given a list with only past events
        eventDateTimeLocations.getFirst().setStartDateTime(FIXED_EVENT_START.minusYears(1));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> eventDateTimeLocationService.isFutureEvent(eventDateTimeLocations));
        assertEquals(ErrorMessage.THE_EVENT_IS_PAST, exception.getMessage());
    }

    @Test
    void testRemoveOldEventDateTimeLocations() {
        // Given
        EventDateTimeLocation dateTime1 = eventDateTimeLocations.getFirst();
        EventDateTimeLocation dateTime2 = EventDateTimeLocation.builder()
                .id(2L)
                .startDateTime(FIXED_EVENT_START.plusDays(2))
                .endDateTime(FIXED_EVENT_END.plusDays(2))
                .location("location")
                .build();

        event.setDateTimes(new ArrayList<>(Arrays.asList(dateTime1, dateTime2)));

        // When
        eventDateTimeLocationService.removeOldEventDateTimeLocations(event, dtoList);

        // Then
        assertEquals(1, event.getDateTimes().size());
        assertEquals(1L, event.getDateTimes().getFirst().getId());
    }

    @Test
    void testUpdateEventDateTimeLocation_ThrowsNotFoundException() {
        // Given
        dtoList.getFirst().setId(1L);

        when(evDateTimeLocRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> eventDateTimeLocationService.updateEventDateTimeLocation(event, dtoList));
        assertTrue(exception.getMessage().contains("DateTimeLocation not found with id: 1"));
        verify(evDateTimeLocRepo).findById(1L);
    }

    @Test
    void testUpdateEventDateTimeLocation_ThrowsBadRequestException() {
        // Given
        Event event = ModelUtils.getEvent();
        event.setId(1L);

        Event differentEvent = ModelUtils.getEvent();
        differentEvent.setId(2L);

        EventDateTimeLocation dateTime = EventDateTimeLocation.builder()
                .id(1L)
                .startDateTime(ZonedDateTime.now().plusDays(1))
                .endDateTime(ZonedDateTime.now().plusDays(2))
                .location("location")
                .event(differentEvent)
                .build();

        List<EventDateTimeLocationRequestDto> dtoList = new ArrayList<>();
        dtoList.add(EventDateTimeLocationRequestDto.builder()
                .id(1L)
                .startDateTime(ZonedDateTime.now().plusDays(1))
                .endDateTime(ZonedDateTime.now().plusDays(2))
                .location("location")
                .build());

        when(evDateTimeLocRepo.findById(1L)).thenReturn(Optional.of(dateTime));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> eventDateTimeLocationService.updateEventDateTimeLocation(event, dtoList));
        assertEquals(ErrorMessage.WRONG_EVENT_ID, exception.getMessage());
        verify(evDateTimeLocRepo).findById(1L);
    }

    @Test
    void testUpdateEventDateTimeLocation_WithExistingAndNewEntities() {
        // Given
        Event event = ModelUtils.getEvent();
        event.setId(1L);
        event.setDateTimes(new ArrayList<>());

        EventDateTimeLocation existingEntity = EventDateTimeLocation.builder()
                .id(1L)
                .startDateTime(ZonedDateTime.now().plusDays(1))
                .endDateTime(ZonedDateTime.now().plusDays(2))
                .location("old location")
                .event(event)
                .build();

        EventDateTimeLocationRequestDto existingDto = EventDateTimeLocationRequestDto.builder()
                .id(1L)
                .startDateTime(ZonedDateTime.now().plusDays(1))
                .endDateTime(ZonedDateTime.now().plusDays(2))
                .location("updated location")
                .build();

        EventDateTimeLocationRequestDto newDto = EventDateTimeLocationRequestDto.builder()
                .startDateTime(ZonedDateTime.now().plusDays(3))
                .endDateTime(ZonedDateTime.now().plusDays(4))
                .location("new location")
                .build();

        List<EventDateTimeLocationRequestDto> dtoList = Arrays.asList(existingDto, newDto);

        when(evDateTimeLocRepo.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(evDateTimeLocRepo.findAllById(anyCollection())).thenReturn(List.of(existingEntity));

        EventDateTimeLocation newEntity = EventDateTimeLocation.builder()
                .startDateTime(newDto.getStartDateTime())
                .endDateTime(newDto.getEndDateTime())
                .location(newDto.getLocation())
                .build();

        // Setup for modelMapper.map(existingDto, existingEntity)
        doAnswer(invocation -> {
            // Update the existingEntity with values from existingDto
            existingEntity.setStartDateTime(existingDto.getStartDateTime());
            existingEntity.setEndDateTime(existingDto.getEndDateTime());
            existingEntity.setLocation(existingDto.getLocation());
            existingEntity.setLink(existingDto.getLink());
            return null;
        }).when(modelMapper).map(eq(existingDto), any(EventDateTimeLocation.class));

        // Setup for modelMapper.map(newDto, EventDateTimeLocation.class)
        when(modelMapper.map(eq(newDto), eq(EventDateTimeLocation.class))).thenReturn(newEntity);

        // When
        eventDateTimeLocationService.updateEventDateTimeLocation(event, dtoList);

        // Then
        verify(evDateTimeLocRepo).findAllById(anyCollection());
        verify(modelMapper).map(eq(existingDto), any(EventDateTimeLocation.class));
        verify(modelMapper).map(eq(newDto), eq(EventDateTimeLocation.class));

        // Verify that the event was set on both entities
        assertEquals(event, existingEntity.getEvent());
        assertTrue(event.getDateTimes().contains(newEntity));
    }
}
