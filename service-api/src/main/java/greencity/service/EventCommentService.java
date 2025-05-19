package greencity.service;

import greencity.dto.event.EventCommentVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
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
    EventCommentDtoResponse save(Long eventId, AddEventCommentDtoRequest comment, UserVO user);

    /**
     * This method updates an event comment.
     *
     * @param commentId the id of the comment that is updated
     * @param comment   the new comment information
     * @param user      the user who is updating the comment
     * @return the updated comment
     * @author Rostyslav Zadyraichuk
     */
    EventCommentDtoResponse update(Long commentId, EditEventCommentDtoRequest comment, UserVO user);

    /**
     * This method allows the current user to like a specific event comment.
     * If the user has already liked the comment, an exception will be thrown.
     * A like is saved in the event_comment_like table.
     *
     * @param user      the user who is liking the comment
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
     * @param user      the user who is removing the like
     * @author Roman Diakov
     */
    void unlikeComment(Long commentId, UserVO user);

    EventCommentVO findById(Long id);
}
