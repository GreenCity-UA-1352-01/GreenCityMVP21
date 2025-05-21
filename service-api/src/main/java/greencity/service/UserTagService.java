package greencity.service;

import greencity.dto.user.UserVO;
import java.util.List;

/**
 * Service for handling user tagging functionality.
 */
public interface UserTagService {
    /**
     * Find users by name for tagging.
     *
     * @param name the name to search for
     * @return list of users with names containing the given string
     * @author Roman Diakov
     */
    List<UserVO> findUsersByName(String name);

    /**
     * Process comment text to find and handle user mentions.
     *
     * @param commentText the text of the comment
     * @param initiatorId the ID of the user who created the comment
     * @param objectType the type of object the comment is on (e.g., "event" or "news")
     * @param objectId the ID of the object the comment is on
     * @param commentId the ID of the comment
     * @author Roman Diakov
     */
    void processUserMentions(String commentText, Long initiatorId, String objectType, Long objectId, Long commentId);
}