package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.entity.EventComment;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddEventCommentDtoResponseMapper extends AbstractConverter<EventComment, AddEventCommentDtoResponse> {
    private ModelMapper modelMapper;

    @Override
    public AddEventCommentDtoResponse convert(EventComment eventComment) {
        return AddEventCommentDtoResponse.builder()
            .id(eventComment.getId())
            .text(eventComment.getText())
            .modifiedDate(eventComment.getModifiedDate())
            .author(modelMapper.map(eventComment.getUser(), EventCommentAuthorDto.class))
            .build();
    }
}
