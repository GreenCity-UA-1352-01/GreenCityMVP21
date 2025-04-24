package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;

    @Override
    public UpdateEventDtoResponse findById(Long id) {
        Event event = getById(id);
        return modelMapper.map(event, UpdateEventDtoResponse.class);
    }

    @Override
    public UpdateEventDtoResponse update(UpdateEventDtoRequest updateEventDtoRequest, List<MultipartFile> images, UserVO user) {
        Event event = getById(updateEventDtoRequest.getId());
        if (user.getRole() != Role.ROLE_ADMIN && !user.getId().equals(event.getInitiator().getId())){
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        // додати логіку оновлення полів
        eventRepository.save(event);
        return  modelMapper.map(event,UpdateEventDtoResponse.class);
    }

    private Event getById(Long id) {
        return eventRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
    }

}
