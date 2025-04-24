package greencity.service;

import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO);
}
