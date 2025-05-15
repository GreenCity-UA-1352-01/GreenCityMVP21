package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventCommentMapper extends AbstractConverter<AddEventCommentDtoRequest, EventComment> {
    @Override
    public EventComment convert(AddEventCommentDtoRequest addEventCommentDtoRequest) {
        return EventComment.builder()
            .text(addEventCommentDtoRequest.getText())
            .deleted(false)
            .build();
    }
}
