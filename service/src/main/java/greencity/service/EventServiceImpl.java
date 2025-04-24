package greencity.service;

import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
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
    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO) {
        return null; //write your impl here
    }

}
