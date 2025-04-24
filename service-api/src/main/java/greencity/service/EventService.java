package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.event.EditEventDto;
import greencity.dto.event.EditEventDtoResponse;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    /**
     * Method for updating Event.
     *
     * @param editEventDto - instance of {@link EditEventDto}.
     * @return instance of {@link EditEventDtoResponse};=.
     */
    EditEventDtoResponse update(EditEventDto editEventDto, List<MultipartFile> images, UserVO user);
}
