package greencity.service;

import greencity.annotations.RatingCalculationEnum;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventCommentVO;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.EventCommentLike;
import greencity.entity.User;
import greencity.enums.NotificationObjectType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.mapping.EventCommentDtoResponseMapper;
import greencity.mapping.EventCommentMapper;
import greencity.exception.exceptions.ConflictException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCommentLikeRepository;
import greencity.repository.EventCommentRepo;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static greencity.constant.AppConstant.AUTHORIZATION;

@Service
@AllArgsConstructor
public class EventCommentServiceImpl implements EventCommentService {
    private EventCommentRepo eventCommentRepo;
    private EventCommentLikeRepository eventCommentLikeRepo;
    private EventService eventService;
    private ModelMapper modelMapper;
    private EventCommentMapper eventCommentMapper;
    private EventCommentDtoResponseMapper eventCommentDtoResponseMapper;
    private final greencity.rating.RatingCalculation ratingCalculation;
    private final HttpServletRequest httpServletRequest;
    private final NotificationService notificationService;

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
    @Transactional
    public EventCommentDtoResponse save(Long eventId,
                                        AddEventCommentDtoRequest comment,
                                        UserVO userVO) {
        EventVO event = eventService.findById(eventId);
        EventComment eventComment = eventCommentMapper.convert(comment);
        eventComment.setUser(modelMapper.map(userVO, User.class));
        eventComment.setEvent(modelMapper.map(event, Event.class));
        if (comment.getParentCommentId() != 0) {
            EventComment parentComment =
                eventCommentRepo.findById(comment.getParentCommentId()).orElseThrow(
                    () -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
            if (parentComment.getParentComment() == null) {
                eventComment.setParentComment(parentComment);
                parentComment.getComments().add(eventComment);
            } else {
                throw new BadRequestException(ErrorMessage.CANNOT_REPLY_THE_REPLY);
            }
        }
        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(
            () -> ratingCalculation.ratingCalculation(RatingCalculationEnum.ADD_COMMENT, userVO, accessToken));
        return eventCommentDtoResponseMapper.convert(eventCommentRepo.saveAndFlush(eventComment));
    }

    /**
     * {@inheritDoc}
     *
     * @param commentId the ID of the comment to update
     * @param comment   the new comment information
     * @param user      the user who is updating the comment
     * @return the updated comment
     * @throws BadRequestException if the comment with the given ID does not exist or is not editable by the user
     * @author Rostyslav Zadyraichuk
     */
    @Override
    @Transactional
    public EventCommentDtoResponse update(Long commentId, EditEventCommentDtoRequest comment, UserVO user) {
        EventComment eventComment = eventCommentRepo.findById(commentId).orElseThrow(
            () -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (!eventComment.getUser().getId().equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_IS_NOT_COMMENT_AUTHOR);
        }
        eventComment.setText(comment.getNewText());
        return eventCommentDtoResponseMapper.convert(eventComment);
    }

    /**
     * This method allows the current user to like a specific event comment.
     * If the user has already liked the comment, a ConflictException is thrown.
     * A like is saved in the event_comment_like table.
     *
     * @param user      the user who is liking the comment
     * @param commentId the ID of the comment to be liked
     * @throws BadRequestException   if the comment is not found
     * @throws ConflictException     if the user has already liked the comment
     * @author Rostyslav Kushpit
     */
    @Override
    @Transactional
    public void likeComment(Long commentId, UserVO user) {
        EventComment eventComment = eventCommentRepo.findById(commentId).orElseThrow(
                () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        if (eventCommentLikeRepo.existsByEventCommentIdAndUserId(commentId, user.getId())) {
            throw new ConflictException(ErrorMessage.COMMENT_ALREADY_LIKED);
        }

        EventCommentLike eventCommentLike = EventCommentLike.builder()
                .eventComment(eventComment)
                .user(modelMapper.map(user, User.class))
                .likedAt(ZonedDateTime.now())
                .build();
        eventCommentLikeRepo.save(eventCommentLike);
    }

    /**
     * {@inheritDoc}
     *
     * @param commentId the ID of the event comment to remove the like from
     * @param user      the user who is removing the like
     * @throws BadRequestException if the comment is not found
     * @throws NotFoundException   if the user has not liked the comment
     * @author Roman Diakov
     */
    @Override
    @Transactional
    public void unlikeComment(Long commentId, UserVO user) {
        EventComment eventComment = eventCommentRepo.findById(commentId).orElseThrow(
                () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));

        if (!eventCommentLikeRepo.existsByEventCommentIdAndUserId(commentId, user.getId())) {
            throw new NotFoundException("You have not liked this comment yet");
        }

        eventCommentLikeRepo.deleteByEventCommentAndUser(eventComment, modelMapper.map(user, User.class));

        notificationService.deleteCommentLikeNotification(user.getId(), eventComment.getUser().getId(),
            NotificationObjectType.EVENT, commentId);
    }

    /**
     * Retrieves an EventCommentVO by its ID.
     *
     * @param id the ID of the comment to retrieve
     * @return EventCommentVO containing the basic data of the comment
     * @throws BadRequestException if the comment is not found
     * @author Rostyslav Kushpit
     */
    @Override
    @Transactional
    public EventCommentVO findById(Long id) {
        EventComment eventComment = eventCommentRepo.findById(id).orElseThrow(
                () -> new BadRequestException(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION));
        return modelMapper.map(eventComment, EventCommentVO.class);
    }
}
