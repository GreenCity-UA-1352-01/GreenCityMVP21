package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.entity.EventComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventCommentDtoResponseMapperTest {

    private static final EventComment EVENT_COMMENT;
    private static final EventCommentDtoResponse RESPONSE_DTO;

    static {
        EVENT_COMMENT = ModelUtils.getEventComment();
        RESPONSE_DTO = ModelUtils.getEventCommentDtoResponse();
    }

    private EventCommentDtoResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EventCommentDtoResponseMapper();
    }

    @Test
    void testConvert() {
        EventCommentDtoResponse actual = assertDoesNotThrow(() -> mapper.convert(EVENT_COMMENT));
        actual.setModifiedDate(RESPONSE_DTO.getModifiedDate());

        assertEquals(RESPONSE_DTO, actual);
    }
}