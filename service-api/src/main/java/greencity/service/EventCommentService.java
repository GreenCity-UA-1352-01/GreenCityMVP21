package greencity.service;

import greencity.dto.event.EventCommentVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;

public interface EventCommentService {
    /**
     * This method adds a comment to the event.
     *
     * @param eventId the id of the event in which the comment is added
     * @param comment the comment that is added
     * @param user    the user who is adding the comment
     * @return the added comment
     * @author Rostyslav Zadyraichuk
     */
    AddEventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user);

    /**
     * This method allows the current user to like a specific event comment.
     * If the user has already liked the comment, an exception will be thrown.
     * A like is saved in the event_comment_like table.
     *
     * @param user the user who is liking the comment
     * @param commentId the ID of the event comment to be liked
     * @author Rostyslav Kushpit
     */
    void likeComment(Long commentId, UserVO user);

    /**
     * This method allows the current user to remove a like from a specific event comment.
     * If the user has not liked the comment, an exception will be thrown.
     * The like is removed from the event_comment_like table.
     *
     * @param commentId the ID of the event comment to remove the like from
     * @param user the user who is removing the like
     * @author Roman Diakov
     */
    void unlikeComment(Long commentId, UserVO user);

    /**
     * This method allows the current user to delete their own comment.
     * If the user is not the author of the comment, an exception will be thrown.
     * The comment is soft deleted by setting the deleted flag to true.
     *
     * @param commentId the ID of the event comment to delete
     * @param user the user who is deleting the comment
     * @throws greencity.exception.exceptions.BadRequestException if the comment is not found
     * @throws greencity.exception.exceptions.UserHasNoPermissionToAccessException if the user is not the author of the comment
     * @author Roman Diakov
     */
    void deleteComment(Long commentId, UserVO user);

    EventCommentVO findById(Long id);
}
