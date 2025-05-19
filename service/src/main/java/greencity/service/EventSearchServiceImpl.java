package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.entity.Event;
import greencity.entity.localization.TagTranslation;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventSearchRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {
    private final EventSearchRepo eventSearchRepo;
    private final ModelMapper modelMapper;

    @Override
    public PageableDto<SearchEventsDto> search(Pageable pageable, String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("Search query must not be empty");
        }

        Page<Event> page = eventSearchRepo.find(pageable, query);

        List<SearchEventsDto> dtos = page.getContent().stream()
                .map(event -> {
                    SearchEventsDto dto = modelMapper.map(event, SearchEventsDto.class);
                    dto.setTags(mapTagsToNames(event));
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageableDto<>(
                dtos,
                page.getTotalElements(),
                page.getNumber(),
                page.getTotalPages()
        );
    }

    private List<String> mapTagsToNames(Event event) {
        return event.getTags().stream()
                .flatMap(tag -> tag.getTagTranslations().stream())
                .map(TagTranslation::getName)
                .collect(Collectors.toList());
    }
}
