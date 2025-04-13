package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.service.LanguageService;
import greencity.service.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.ZonedDateTime;
import java.util.List;


@WebMvcTest(controllers = SearchController.class)
@ContextConfiguration(classes = {SearchController.class})
@WithMockUser
class SearchControllerTest {
    @MockBean
    SearchService searchService;
    @MockBean
    private LanguageService languageService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void search_ValidTest_ShouldReturnSearchResponseDto() throws Exception {
        Mockito.when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("en", "ua", "pl"));

        SearchNewsDto news = SearchNewsDto.builder()
                .id(1L)
                .title("Save the planet")
                .author(new EcoNewsAuthorDto(1L, "John Doe"))
                .creationDate(ZonedDateTime.now())
                .tags(List.of("eco", "planet"))
                .build();

        SearchResponseDto searchResponseDto = SearchResponseDto.builder()
                .ecoNews(List.of(news))
                .countOfResults(1L)
                .build();

        Mockito.when(searchService.search(eq("eco"), eq("en"))).thenReturn(searchResponseDto);
        mockMvc.perform(get("/search")
                        .param("searchQuery", "eco")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.countOfResults").value(1))
                .andExpect(jsonPath("$.ecoNews[0].title").value("Save the planet"))
                .andExpect(jsonPath("$.ecoNews[0].author.name").value("John Doe"))
                .andExpect(jsonPath("$.ecoNews[0].tags[0]").value("eco"));

        verify(searchService, times(1)).search(eq("eco"), eq("en"));
    }

    @Test
    void search_InValidTest_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/search")
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_InvalidTest_invalidLanguage_shouldReturnBadRequest() throws Exception {
        Mockito.when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("en", "ua", "pl"));

        mockMvc.perform(get("/search")
                        .param("searchQuery", "eco")
                        .header("Accept-Language", "de"))
                .andExpect(status().isBadRequest());
    }



    @Test
    void searchEcoNews_ValidTest_shouldReturnPaginatedResults() throws Exception {
        Mockito.when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("en", "ua", "pl"));

        SearchNewsDto news = SearchNewsDto.builder()
                .id(1L)
                .title("Save the planet")
                .author(new EcoNewsAuthorDto(1L, "John Doe"))
                .creationDate(ZonedDateTime.now())
                .tags(List.of("eco", "planet"))
                .build();

        PageableDto<SearchNewsDto> dto = new PageableDto<>(
                List.of(news),
                1L,
                0,
                1
        );
        Mockito.when(searchService.searchAllNews(any(), eq("eco"), eq("en"))).thenReturn(dto);
        mockMvc.perform(get("/search/econews")
                        .param("searchQuery", "eco")
                        .param("page", "0")
                        .param("size", "5")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page[0].title").value("Save the planet"))
                .andExpect(jsonPath("$.page[0].author.name").value("John Doe"));

        verify(searchService, times(1)).searchAllNews(any(), eq("eco"), eq("en"));
    }

    @Test
    void searchEcoNews_InValidTest_missingQuery_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/search/econews")
                        .param("page", "0")
                        .param("size", "5")
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchEcoNews_InValidTest_invalidLanguage_shouldReturnBadRequest() throws Exception {
        Mockito.when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("en", "ua", "pl"));

        mockMvc.perform(get("/search/econews")
                        .param("searchQuery", "eco")
                        .param("page", "0")
                        .param("size", "5")
                        .header("Accept-Language", "de"))
                .andExpect(status().isBadRequest());
    }


}