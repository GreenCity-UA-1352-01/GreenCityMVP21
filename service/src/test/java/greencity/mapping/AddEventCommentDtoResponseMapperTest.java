package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.entity.EventComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddEventCommentDtoResponseMapperTest {

    private static final EventComment EVENT_COMMENT;
    private static final AddEventCommentDtoResponse RESPONSE_DTO;

    static {
        EVENT_COMMENT = ModelUtils.getEventComment();
        RESPONSE_DTO = ModelUtils.getAddEventCommentDtoResponse();
    }

    private AddEventCommentDtoResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AddEventCommentDtoResponseMapper();
    }

    @Test
    void testConvert() {
        AddEventCommentDtoResponse actual = assertDoesNotThrow(() -> mapper.convert(EVENT_COMMENT));
        actual.setModifiedDate(RESPONSE_DTO.getModifiedDate());

        assertEquals(RESPONSE_DTO, actual);
    }
}