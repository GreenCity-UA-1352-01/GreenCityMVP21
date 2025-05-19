package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.mapping.EventCommentDtoResponseMapper;
import greencity.mapping.EventCommentMapper;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class EventCommentServiceImplTest {

    private static final UserVO USER;
    private static final EventVO EVENT;
    private static final EventComment EVENT_COMMENT;

    static {
        USER = ModelUtils.getUserVO();
        EVENT = ModelUtils.getEventVO();
        EVENT_COMMENT = ModelUtils.getEventComment();
    }

    @Mock
    private EventCommentRepo eventCommentRepository;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private EventService eventService;
    @Spy
    private EventCommentDtoResponseMapper eventCommentDtoResponseMapper;
    @Spy
    private EventCommentMapper eventCommentMapper;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentServiceImpl eventCommentService;

    @Test
    void testSave_withoutParentComment() {
        LocalDateTime now = LocalDateTime.now();
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        request.setParentCommentId(0L);
        EventComment eventComment = eventCommentMapper.convert(request);
        eventComment.setId(1L);
        eventComment.setUser(modelMapper.map(USER, User.class));
        eventComment.setCreatedDate(now);
        eventComment.setModifiedDate(now);

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.saveAndFlush(any(EventComment.class))).thenReturn(eventComment);

        EventCommentDtoResponse actual = eventCommentService.save(EVENT.getId(), request, USER);
        EventCommentDtoResponse expected = ModelUtils.getEventCommentDtoResponse();
        expected.setModifiedDate(now);
        expected.setModifiedDate(actual.getModifiedDate());

        assertEquals(expected, actual);
        verify(eventCommentRepository).saveAndFlush(any(EventComment.class));
    }

    @Test
    void testSave_withNotExistingParentComment() {
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = eventCommentMapper.convert(request);

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.findById(request.getParentCommentId()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> eventCommentService.save(EVENT.getId(), request, USER));
        verify(eventCommentRepository, never()).save(eventComment);
    }

    @Test
    void testSave_withExistingParentCommentWithoutDoubleParent() {
        LocalDateTime now = LocalDateTime.now();
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = eventCommentMapper.convert(request);
        eventComment.setId(1L);
        eventComment.setUser(modelMapper.map(USER, User.class));
        eventComment.setCreatedDate(now);
        eventComment.setModifiedDate(now);

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.saveAndFlush(any(EventComment.class))).thenReturn(eventComment);
        when(eventCommentRepository.findById(request.getParentCommentId()))
            .thenReturn(Optional.of(ModelUtils.getEventComment()));

        EventCommentDtoResponse actual = eventCommentService.save(EVENT.getId(), request, USER);
        EventCommentDtoResponse expected = ModelUtils.getEventCommentDtoResponse();
        expected.setModifiedDate(now);
        expected.setModifiedDate(actual.getModifiedDate());

        assertEquals(expected, actual);
        verify(eventCommentRepository).saveAndFlush(any(EventComment.class));
    }

    @Test
    void testSave_withExistingParentCommentWithDoubleParent() {
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = eventCommentMapper.convert(request);
        eventComment.setParentComment(eventComment);

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.findById(request.getParentCommentId()))
            .thenReturn(Optional.of(eventComment));

        Exception actual = assertThrows(BadRequestException.class,
            () -> eventCommentService.save(EVENT.getId(), request, USER));
        assertEquals(ErrorMessage.CANNOT_REPLY_THE_REPLY, actual.getMessage());
        verify(eventCommentRepository, never()).save(eventComment);
    }

    @Test
    void testUpdate() {
        EditEventCommentDtoRequest comment = ModelUtils.getEditEventCommentDtoRequest();

        when(eventCommentRepository.findById(EVENT_COMMENT.getId())).thenReturn(Optional.of(EVENT_COMMENT));

        EventCommentDtoResponse actual = eventCommentService.update(EVENT_COMMENT.getId(), comment, USER);
        assertEquals(comment.getNewText(), actual.getText());
    }

    @Test
    void testUpdate_withNotExistingComment() {
        EditEventCommentDtoRequest comment = ModelUtils.getEditEventCommentDtoRequest();

        when(eventCommentRepository.findById(EVENT_COMMENT.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> eventCommentService.update(EVENT_COMMENT.getId(), comment, USER));
    }

    @Test
    void testUpdate_withNotOwner() {
        EditEventCommentDtoRequest comment = ModelUtils.getEditEventCommentDtoRequest();
        UserVO notOwner = ModelUtils.getUserVO().setId(USER.getId() + 1);

        when(eventCommentRepository.findById(EVENT_COMMENT.getId())).thenReturn(Optional.of(EVENT_COMMENT));

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> eventCommentService.update(EVENT_COMMENT.getId(), comment, notOwner));
    }
}