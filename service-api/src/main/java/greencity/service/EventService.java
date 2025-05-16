package greencity.service;

import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;

public interface EventService {
    CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO);

    UpdateEventDtoResponse getUpdateEventDto(Long id);

    /**
     * Method for updating Event.
     *
     * @param updateEventDtoRequest - instance of {@link UpdateEventDtoRequest}.
     * @return instance of {@link UpdateEventDtoResponse};=.
     */
    UpdateEventDtoResponse updateEvent(UpdateEventDtoRequest updateEventDtoRequest,
                                       List<MultipartFile> images,
                                       UserVO user);

    /**
     * Deletes an event by its ID.
     *
     * @param id   the ID of the event to be deleted
     * @param user the user requesting the deletion
     *
     * @author Rostyslav Zadyraichuk
     */
    void deleteById(Long id, UserVO user);

    /**
     * Finds an event by its ID.
     *
     * @param id the ID of the event to find
     * @return the {@link EventVO} associated with the given ID
     *
     * @author Rostyslav Zadyraichuk
     */
    EventVO findById(Long id);

    /**
     * Like an event.
     *
     * @param id   the ID of the event to be liked
     * @param user the user requesting the like
     *
     * @author Rostyslav Kushpit
     */
    void likeEvent(Long id, UserVO user);

    /**
     * Unlike an event.
     *
     * @param id   the ID of the event to be unliked
     * @param user the user requesting the unlike
     *
     * @author Roman Diakov
     */
    void unlikeEvent(Long id, UserVO user);
}
