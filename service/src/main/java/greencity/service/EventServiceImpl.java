package greencity.service;

import com.ctc.wstx.util.ElementId;
import greencity.dto.event.CreateEventDto;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.repository.EventRepository;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.internal.EntityManagerMessageLogger_$logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final TagsRepo tagsRepo;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CreateEventDtoResponse createEvent(CreateEventDto dto, List<MultipartFile> images, UserVO userVO) {
        User initiator = getUserById(userVO.getId());
        Event event = buildBaseEvent(dto, initiator);
        List<String> imagePaths = handleImages(images, event);
        handleDates(dto.getDates(), event);
        handleTags(dto.getTags(), event);

        Event savedEvent = eventRepository.save(event);
        return buildResponse(dto, savedEvent, imagePaths);
    }

    private User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private Event buildBaseEvent(CreateEventDto dto, User initiator) {
        Event event = modelMapper.map(dto, Event.class);
        event.setInitiator(initiator);
        return event;
    }

    private List<String> handleImages(List<MultipartFile> files, Event event) {
        List<EventImage> eventImages = new ArrayList<>();
        List<String> imagePaths = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return imagePaths;
        } else {

            for (MultipartFile file : files) {
                String imagePath = saveImage(file);
                imagePaths.add(imagePath);

                EventImage eventImage = new EventImage();
                eventImage.setImagePath(imagePath);
                eventImages.add(eventImage);
            }
        }
        event.getEventImages().addAll(eventImages);
        return imagePaths;
    }

    private void handleDates(List<EventDateLocationDto> dates, Event event) {
        List<EventDateTimeLocation> dateTimes = dates.stream()
                .map(dto -> EventDateTimeLocation.builder()
                        .startDateTime(dto.getStartDateTime())
                        .endDateTime(dto.getEndDateTime())
                        .location(dto.getLocation())
                        .link(dto.getOnlineLink())
                        .event(event)
                        .build())
                .collect(Collectors.toList());

        event.setDateTimes(dateTimes);
    }

    private void handleTags(List<TagUaEnDto> tagsDto, Event event) {
        if (tagsDto != null) {
            Set<Tag> tags = tagsDto.stream()
                    .map(tagDto -> tagsRepo.findById(tagDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + tagDto.getId())))
                    .collect(Collectors.toSet());
            event.setTags(tags);
        }
    }

    private CreateEventDtoResponse buildResponse(CreateEventDto dto, Event event, List<String> imagePaths) {
        return CreateEventDtoResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .open(event.isOpen())
                .place(dto.getPlace())
                .online(dto.getOnline())
                .initiativeTypes(dto.getInitiativeTypes())
                .tags(dto.getTags())
                .dates(dto.getDates())
                .images(imagePaths)
                .createdDateTime(ZonedDateTime.now())
                .build();
    }
    private String saveImage(MultipartFile file) {
        String imagePath = "/images/" + file.getOriginalFilename();
        return imagePath;
    }


}


