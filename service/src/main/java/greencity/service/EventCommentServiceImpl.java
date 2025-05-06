package greencity.service;

import greencity.annotations.NotifyUser;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventCommentRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;

    /**
     * {@inheritDoc}
     *
     * @param eventId the ID of the event to which the comment is related
     * @param comment the DTO containing the details of the comment to add
     * @param userVO  the user who is adding the comment
     * @return the response DTO containing the details of the saved comment
     * @author Rostyslav Zadyraichuk
     */
    @Override
    public AddEventCommentDtoResponse save(Long eventId,
                                           AddEventCommentDtoRequest comment,
                                           UserVO userVO) {
        EventVO event = eventService.findById(eventId);
        EventComment eventComment = modelMapper.map(comment, EventComment.class);
        eventComment.setUser(modelMapper.map(userVO, User.class));
        eventComment.setEvent(modelMapper.map(event, Event.class));
        if (comment.getParentCommentId() != 0) {
            EventComment parentComment =
                eventCommentRepo.findById(comment.getParentCommentId()).orElseThrow(
                    () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.getParentComment() == null) {
                eventComment.setParentComment(parentComment);
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accessToken));
        return modelMapper.map(eventCommentRepo.save(eventComment), AddEventCommentDtoResponse.class);
    }
}
