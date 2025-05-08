package greencity.service;

import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * @author Rostyslav Zadyraichuk
     */
    void deleteById(Long id, UserVO user);

    void cancelEventById(Long id, UserVO user);

    EventVO findById(Long id);
}
