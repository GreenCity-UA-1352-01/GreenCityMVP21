package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final ModelMapper modelMapper;

    private final EventRepository eventRepository;

    private final FileService fileService;

    /**
     * {@inheritDoc}
     * Method check user is owner of event or has ADMIN role.
     * All images related to event will be deleted from external file storage.
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
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        eventRepository.deleteById(id);

        if (event.getMainImage() != null) {
            fileService.delete(event.getMainImage().getImagePath());
        }
        event.getEventImages()
            .forEach(image -> fileService.delete(image.getImagePath()));
    }
}
