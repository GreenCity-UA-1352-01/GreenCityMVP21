package greencity.service;


import greencity.dto.event.EventDateLocationDto;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.entity.Event;
import greencity.entity.EventDateTimeLocation;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EventDateLocationDtoMapper;
import greencity.repository.EventDateTimeLocationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventDateTimeLocationService {
    private final EventDateLocationDtoMapper eventDateLocationDtoMapper;
    private final EventDateTimeLocationRepo evDateTimeLocRepo;

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
        //  collect ids that come from dto
        Set<Long> dtoIds = dtoList.stream()
                .map(EventDateTimeLocationRequestDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // delete old ids that don't match with new (orphanRemoval)
        event.getDateTimes().removeIf(edtl ->
                edtl.getId() != null && !dtoIds.contains(edtl.getId())
        );

        // Collect all ids to map
        Map<Long, EventDateTimeLocation> existing = event.getDateTimes().stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(EventDateTimeLocation::getId, Function.identity()));

        // update existing id. Check ids from dtoList and matching with existing in the map. Or create new.
        for (EventDateTimeLocationRequestDto dto : dtoList) {
            if (dto.getId() != null) {
                EventDateTimeLocation toUpdate = existing.get(dto.getId());
                if (toUpdate == null) {
                    throw new BadRequestException(
                            "Немає дати з id=" + dto.getId() + " або вона належить іншому івенту");
                }
                toUpdate.setStartDateTime(dto.getStartDateTime());
                toUpdate.setEndDateTime(dto.getEndDateTime());
                toUpdate.setLocation(dto.getLocation());
                toUpdate.setLink(dto.getLink());
            } else {
                EventDateTimeLocation created = EventDateTimeLocation.builder()
                        .startDateTime(dto.getStartDateTime())
                        .endDateTime(dto.getEndDateTime())
                        .location(dto.getLocation())
                        .link(dto.getLink())
                        .event(event)
                        .build();
                event.getDateTimes().add(created);
            }
        }
    }
}
