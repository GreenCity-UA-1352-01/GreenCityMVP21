package greencity.service;

import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;

public interface EventService {

    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO);

    UpdateEventDtoResponse getUpdateEventDto(Long id);

    /**
     * Method for updating Event.
     *
     * @param updateEventDtoRequest - instance of {@link UpdateEventDtoRequest}.
     * @return instance of {@link UpdateEventDtoResponse};=.
     */
    UpdateEventDtoResponse updateEvent(UpdateEventDtoRequest updateEventDtoRequest, List<MultipartFile> images, UserVO user);
}
