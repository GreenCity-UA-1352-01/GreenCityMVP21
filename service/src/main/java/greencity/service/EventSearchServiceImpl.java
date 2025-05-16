package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.entity.Event;
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
        Page<Event> page = eventSearchRepo.find(pageable, query);

        List<SearchEventsDto> dtos = page.getContent().stream()
                .map(event -> modelMapper.map(event, SearchEventsDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                dtos,
                page.getTotalElements(),
                page.getNumber(),
                page.getTotalPages()
        );
    }
}
