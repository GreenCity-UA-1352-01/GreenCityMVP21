package greencity.service;

import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    UpdateEventDtoResponse findUpdateEventDtoResponseById(Long id);

    /**
     * Method for updating Event.
     *
     * @param updateEventDtoRequest - instance of {@link UpdateEventDtoRequest}.
     * @return instance of {@link UpdateEventDtoResponse};=.
     */
    UpdateEventDtoResponse update(UpdateEventDtoRequest updateEventDtoRequest, List<MultipartFile> images, UserVO user);
}
