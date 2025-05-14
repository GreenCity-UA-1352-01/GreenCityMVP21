package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import greencity.repository.EventCommentRepo;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventCommentMapper extends AbstractConverter<AddEventCommentDtoRequest, EventComment> {
    private EventCommentRepo eventCommentRepo;

    @Override
    public EventComment convert(AddEventCommentDtoRequest addEventCommentDtoRequest) {
        return EventComment.builder()
            .text(addEventCommentDtoRequest.getText())
            .createdDate(ZonedDateTime.now())
            .modifiedDate(ZonedDateTime.now())
            .deleted(false)
            .build();
    }
}
