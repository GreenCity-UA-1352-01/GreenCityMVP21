package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.ApiPageableWithLocale;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;

@RestController
@RequestMapping("/search")
@AllArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * Method for search.
     *
     * @param searchQuery query to search.
     * @return list of {@link SearchResponseDto}.
     */
    @Operation(summary = "Search.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiLocale
    @GetMapping("")
    public ResponseEntity<SearchResponseDto> search(
            @Parameter(description = "Query to search") @RequestParam String searchQuery,
            @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK).body(searchService.search(searchQuery, locale.getLanguage()));
    }

    /**
     * Method for search.
     *
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchNewsDto} instances.
     */
    @Operation(summary = "Search Eco news.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/econews")
    @ApiPageableWithLocale
    public ResponseEntity<PageableDto<SearchNewsDto>> searchEcoNews(
        @Parameter(hidden = true) Pageable pageable,
        @Parameter(description = "Query to search") @RequestParam String searchQuery,
        @Parameter(hidden = true) @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchService.searchAllNews(pageable, searchQuery, locale.getLanguage()));
    }

    /**
     * Method for search.
     *
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchEventsDto} instances.
     */
    @Operation(summary = "Search Events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @Parameters({
            @Parameter(name = "page", schema = @Schema(type = "int", minimum = "0", defaultValue = "0"),
                    description = "Page index you want to retrieve [0..N]. " +
                            "If page index is less than 0 or not specified then default value is used!"),
            @Parameter(name = "size", schema = @Schema(type = "int", minimum = "1", defaultValue = "10"),
                    description = "Number of records per page [1..100]. " +
                            "If size is less than 1 or not specified then default value is used!If size is bigger than 100, size becomes 100."),
    })
    @GetMapping("/events")
    public ResponseEntity<PageableDto<SearchEventsDto>> searchEvent(
            @Parameter(hidden = true) Pageable pageable,
            @Parameter(description = "Query to search") @RequestParam String searchQuery) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(searchService.searchAllEvents(pageable, searchQuery));
    }
}
