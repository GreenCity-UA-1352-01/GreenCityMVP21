package greencity.service;

import greencity.dto.event.UpdateEventDto;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    UpdateEventDtoResponse findById(Long id);

    /**
     * Method for updating Event.
     *
     * @param updateEventDto - instance of {@link UpdateEventDto}.
     * @return instance of {@link UpdateEventDtoResponse};=.
     */
    UpdateEventDtoResponse update(UpdateEventDto updateEventDto, List<MultipartFile> images, UserVO user);
}
