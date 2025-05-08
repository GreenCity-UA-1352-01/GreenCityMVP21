package greencity.service;

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
}
