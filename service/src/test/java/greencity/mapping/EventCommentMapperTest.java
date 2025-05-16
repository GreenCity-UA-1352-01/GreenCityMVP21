package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventCommentMapperTest {
    private static final EventComment EVENT_COMMENT;
    private static final AddEventCommentDtoRequest REQUEST_DTO;

    static {
        EVENT_COMMENT = ModelUtils.getEventComment();
        REQUEST_DTO = ModelUtils.getAddEventCommentDtoRequest();
    }

    private EventCommentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EventCommentMapper();
    }

    @Test
    void testConvert() {
        EventComment actual = assertDoesNotThrow(() -> mapper.convert(REQUEST_DTO));
        actual.setEvent(EVENT_COMMENT.getEvent());
        actual.setUser(EVENT_COMMENT.getUser());
        actual.setCreatedDate(EVENT_COMMENT.getCreatedDate());
        actual.setModifiedDate(EVENT_COMMENT.getModifiedDate());
        actual.setId(EVENT_COMMENT.getId());

        assertEquals(EVENT_COMMENT, actual);
    }
}