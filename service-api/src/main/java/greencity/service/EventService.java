package greencity.service;

import greencity.dto.user.UserVO;

public interface EventService {
    /**
     * Deletes an event by its ID.
     *
     * @param id   the ID of the event to be deleted
     * @param user the user requesting the deletion
     *
     * @author Rostyslav Zadyraichuk
     */
    void deleteById(Long id, UserVO user);
}
