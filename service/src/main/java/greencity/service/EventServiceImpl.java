package greencity.service;

import greencity.dto.event.EditEventDto;
import greencity.dto.event.EditEventDtoResponse;
import greencity.dto.user.UserVO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper modelMapper;

    @Override
    public EditEventDtoResponse update(EditEventDto editEventDto, List<MultipartFile> images, UserVO user) {
        return null;
    }
}
