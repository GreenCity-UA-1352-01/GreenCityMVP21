package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.mapping.AddEventCommentDtoResponseMapper;
import greencity.mapping.EventCommentMapper;
import greencity.rating.RatingCalculation;
import greencity.repository.EventCommentRepo;
import jakarta.servlet.http.HttpServletRequest;
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

    static {
        USER = ModelUtils.getUserVO();
        EVENT = ModelUtils.getEventVO();
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
    private AddEventCommentDtoResponseMapper addEventCommentDtoResponseMapper;
    @Spy
    private EventCommentMapper eventCommentMapper;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EventCommentServiceImpl eventCommentService;

    @Test
    void testSave_withoutParentComment() {
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        request.setParentCommentId(0L);
        EventComment eventComment = eventCommentMapper.convert(request);
        eventComment.setId(1L);
        eventComment.setUser(modelMapper.map(USER, User.class));

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.save(any(EventComment.class))).thenReturn(eventComment);

        AddEventCommentDtoResponse actual = eventCommentService.save(EVENT.getId(), request, USER);
        AddEventCommentDtoResponse expected = ModelUtils.getAddEventCommentDtoResponse();
        expected.setModifiedDate(actual.getModifiedDate());

        assertEquals(expected, actual);
        verify(eventCommentRepository).save(any(EventComment.class));
    }

    @Test
    void testSave_withNotExistingParentComment() {
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = eventCommentMapper.convert(request);

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.findById(request.getParentCommentId()))
            .thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class,
            () -> eventCommentService.save(EVENT.getId(), request, USER));
        verify(eventCommentRepository, never()).save(eventComment);
    }

    @Test
    void testSave_withExistingParentCommentWithoutDoubleParent() {
        AddEventCommentDtoRequest request = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = eventCommentMapper.convert(request);
        eventComment.setId(1L);
        eventComment.setUser(modelMapper.map(USER, User.class));

        when(eventService.findById(EVENT.getId())).thenReturn(EVENT);
        when(eventCommentRepository.save(any(EventComment.class))).thenReturn(eventComment);
        when(eventCommentRepository.findById(request.getParentCommentId()))
            .thenReturn(Optional.of(ModelUtils.getEventComment()));

        AddEventCommentDtoResponse actual = eventCommentService.save(EVENT.getId(), request, USER);
        AddEventCommentDtoResponse expected = ModelUtils.getAddEventCommentDtoResponse();
        expected.setModifiedDate(actual.getModifiedDate());

        assertEquals(expected, actual);
        verify(eventCommentRepository).save(any(EventComment.class));
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
}