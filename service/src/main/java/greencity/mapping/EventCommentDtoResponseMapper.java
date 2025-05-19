package greencity.mapping;

import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventCommentDtoResponseMapper extends AbstractConverter<EventComment, EventCommentDtoResponse> {
    @Override
    public EventCommentDtoResponse convert(EventComment eventComment) {
        User author = eventComment.getUser();
        return EventCommentDtoResponse.builder()
            .id(eventComment.getId())
            .text(eventComment.getText())
            .modifiedDate(eventComment.getModifiedDate())
            .author(EventCommentAuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .userProfilePicturePath(author.getProfilePicturePath())
                .build())
            .modified(!eventComment.getModifiedDate().isEqual(eventComment.getCreatedDate()))
            .deleted(eventComment.getDeleted())
            .build();
    }
}
