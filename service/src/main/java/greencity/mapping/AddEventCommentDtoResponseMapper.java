package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddEventCommentDtoResponseMapper extends AbstractConverter<EventComment, AddEventCommentDtoResponse> {
    @Override
    public AddEventCommentDtoResponse convert(EventComment eventComment) {
        User author = eventComment.getUser();
        return AddEventCommentDtoResponse.builder()
            .id(eventComment.getId())
            .text(eventComment.getText())
            .modifiedDate(eventComment.getModifiedDate())
            .author(EventCommentAuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .userProfilePicturePath(author.getProfilePicturePath())
                .build())
            .build();
    }
}
