package greencity.service;

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
