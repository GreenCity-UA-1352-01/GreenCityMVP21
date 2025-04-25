package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final ModelMapper modelMapper;

    private final EventRepository eventRepository;

    /**
     * {@inheritDoc}
     * Method check user is owner of event or has ADMIN role
     *
     * @param id   the ID of the event to be deleted
     * @param user the user requesting the deletion
     *
     * @author Rostyslav Zadyraichuk
     */
    @Override
    public void deleteById(Long id, UserVO user) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));

        if (!event.getInitiator().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventRepository.deleteById(id);
    }
}
