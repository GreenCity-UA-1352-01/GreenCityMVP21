package greencity.mapping;

import greencity.dto.event.EventDateLocationDto;
import greencity.entity.EventDateTimeLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDateLocationDtoMapper extends AbstractConverter<EventDateTimeLocation, EventDateLocationDto> {
    @Override
    public EventDateLocationDto convert(EventDateTimeLocation entity) {
        return EventDateLocationDto.builder()
                .startDateTime(entity.getStartDateTime())
                .endDateTime(entity.getEndDateTime())
                .location(entity.getLocation())
                .onlineLink(entity.getLink())
                .build();
    }
}

