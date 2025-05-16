package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import org.springframework.data.domain.Pageable;

public interface EventSearchService {
    public PageableDto<SearchEventsDto> search(Pageable pageable, String query);
}
