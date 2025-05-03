package greencity.service;


import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.entity.Event;
import greencity.entity.EventDateTimeLocation;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EventDateLocationDtoMapper;
import greencity.repository.EventDateTimeLocationRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class EventDateTimeLocationService {
    private final EventDateLocationDtoMapper eventDateLocationDtoMapper;
    private final EventDateTimeLocationRepo evDateTimeLocRepo;
    public final ModelMapper modelMapper;

    public void handleDateTimeLocations(List<EventDateLocationDto> dates, Event event) {
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

    public List<EventDateLocationDto> getAllDates(Event event) {
        return event.getDateTimes().stream()
                .map(eventDateLocationDtoMapper::convert)
                .collect(Collectors.toList());
    }


    public void updateEventDateTimeLocation(Event event,
                                            List<EventDateTimeLocationRequestDto> dtoList) {
        checkStartEndDates(dtoList);
        matchDateTimeWithEventCheck(event, dtoList);
        removeOldEventDateTimeLocations(event, dtoList);

        Map<Long, EventDateTimeLocationRequestDto> dtoById = dtoList.stream()
                .filter(dto -> dto.getId() != null)
                .collect(Collectors.toMap(EventDateTimeLocationRequestDto::getId, Function.identity()));
        List<EventDateTimeLocation> existingEntities = evDateTimeLocRepo.findAllById(dtoById.keySet());
        for (EventDateTimeLocation entity : existingEntities) {
            modelMapper.map(dtoById.get(entity.getId()), entity);
            entity.setEvent(event);
        }
        dtoList.stream()
                .filter(dto -> dto.getId() == null)
                .forEach(dto -> {
                    EventDateTimeLocation newEntity = modelMapper.map(dto, EventDateTimeLocation.class);
                    ;
                    newEntity.setEvent(event);
                    event.getDateTimes().add(newEntity);
                });
    }

    private void matchDateTimeWithEventCheck(Event event, List<EventDateTimeLocationRequestDto> dtoList) {
        for (EventDateTimeLocationRequestDto dto : dtoList) {
            if (dto.getId() != null) {
                EventDateTimeLocation entity = evDateTimeLocRepo.findById(dto.getId())
                        .orElseThrow(() -> new NotFoundException("DateTimeLocation not found with id: " + dto.getId()));

                if (!entity.getEvent().getId().equals(event.getId())) {
                    throw new BadRequestException(ErrorMessage.WRONG_EVENT_ID);
                }
            }
        }
    }

    public void removeOldEventDateTimeLocations(Event event, List<EventDateTimeLocationRequestDto> dtoList) {
        Set<Long> ids = dtoList.stream()
                .map(EventDateTimeLocationRequestDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        event.getDateTimes().removeIf(eventDTL -> !ids.contains(eventDTL.getId()));
    }

    public void isFutureEvent(List<EventDateTimeLocation> dtoList) {
        boolean isFutureEvent = dtoList.stream()
                .anyMatch(dateTime -> dateTime.getStartDateTime().isAfter(ZonedDateTime.now()));
        if (!isFutureEvent) {
            throw new BadRequestException(ErrorMessage.CANNOT_EDIT_PAST_EVENT);
        }
    }

    public void checkStartEndDates(List<EventDateTimeLocationRequestDto> dtoList) {
        boolean isFutureEvent = dtoList.stream()
                .anyMatch(dateTime -> dateTime.getStartDateTime().
                        isAfter(dateTime.getEndDateTime()) ||
                        dateTime.getStartDateTime().equals(dateTime.getEndDateTime()));
        if (isFutureEvent) {
            throw new BadRequestException(ErrorMessage.START_DATE_TIME_AFTER_END_DATE_TIME);
        }
    }
}
