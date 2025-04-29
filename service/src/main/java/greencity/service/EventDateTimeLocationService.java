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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventDateTimeLocationService {
    private final EventDateLocationDtoMapper eventDateLocationDtoMapper;
    private final EventDateTimeLocationRepo evDateTimeLocRepo;

    protected void handleDateTimeLocations(List<EventDateLocationDto> dates, Event event) {
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

    protected List<EventDateLocationDto> getAllDates(Event event) {
        return event.getDateTimes().stream()
                .map(eventDateLocationDtoMapper::convert)
                .collect(Collectors.toList());
    }

    protected void updateEventDateTimeLocation(Event event, List<EventDateTimeLocationRequestDto> dtoList) {
//        List<EventDateTimeLocation> updatedDates = dtoList.stream()
//                .map(dateTimeDto -> {
//                    EventDateTimeLocation dateTimeLocation = event.getDateTimes().stream()
//                            .filter(dt -> dt.getId() != null && dt.getId().equals(dateTimeDto.getId()))
//                            .findFirst()
//                            .orElseGet(() -> {
//                                EventDateTimeLocation newDateTime = new EventDateTimeLocation();
//                                newDateTime.setEvent(event);
//                                return newDateTime;
//                            });
//                    dateTimeLocation.setStartDateTime(dateTimeDto.getStartDateTime());
//                    dateTimeLocation.setEndDateTime(dateTimeDto.getEndDateTime());
//                    dateTimeLocation.setLocation(dateTimeDto.getLocation());
//                    dateTimeLocation.setLink(dateTimeDto.getLink());
//                    return dateTimeLocation;
//                })
//                .toList();
//        event.getDateTimes().clear();
//        event.getDateTimes().addAll(updatedDates);
        Set<Long> existingIds = event.getDateTimes().stream()
                .map(EventDateTimeLocation::getId)
                .collect(Collectors.toSet());

        List<EventDateTimeLocation> resultDateTimes = new ArrayList<>();

        for (EventDateTimeLocationRequestDto dto : dtoList) {
            Long dtoId = dto.getId();

            if (dtoId != null) {
                if (!existingIds.contains(dtoId)) {
                    throw new BadRequestException("The event date-time location id " + dtoId + " does not exist");
                }
                EventDateTimeLocation entity = evDateTimeLocRepo.findById(dtoId)
                        .orElseThrow(() -> new NotFoundException("The EventDateTimeLocation with id " + dtoId + " does not exist"));

                entity.setStartDateTime(dto.getStartDateTime());
                entity.setEndDateTime(dto.getEndDateTime());
                entity.setLocation(dto.getLocation());
                entity.setLink(dto.getLink());

                resultDateTimes.add(entity);
            }else {
                EventDateTimeLocation newEntity = EventDateTimeLocation.builder()
                        .event(event)
                        .startDateTime(dto.getStartDateTime())
                        .endDateTime(dto.getEndDateTime())
                        .location(dto.getLocation())
                        .link(dto.getLink())
                        .build();
                resultDateTimes.add(newEntity);
            }


        }


    }


}
