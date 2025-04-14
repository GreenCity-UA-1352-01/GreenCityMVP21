package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;


import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class SearchNewsDtoMapperTest {
    private SearchNewsDtoMapper searchNewsDtoMapper;
    private EcoNews ecoNews;
    private User author;

    @BeforeEach
    void setUp(){
        searchNewsDtoMapper = new SearchNewsDtoMapper();
        author = ModelUtils.getUser();
        ecoNews = ModelUtils.getEcoNews();
    }

    @Test
    void testConvertFromEntityToDto(){
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        SearchNewsDto dto = searchNewsDtoMapper.convert(ecoNews);

        assertNotNull(dto);
        assertEquals(ecoNews.getId(), dto.getId());
        assertEquals(ecoNews.getTitle(), dto.getTitle());
        assertEquals(author.getName(), dto.getAuthor().getName());
        assertEquals(ecoNews.getCreationDate(), dto.getCreationDate());
        assertTrue(dto.getTags().contains("News"));
        assertFalse(dto.getTags().contains("Новини"));
    }

    @Test
    void testConvertFromEntityToDto_EmptyTags(){
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        EcoNews emptyTagsEcoNews = ecoNews;
        emptyTagsEcoNews.setTags(List.of());

        SearchNewsDto dto = searchNewsDtoMapper.convert(emptyTagsEcoNews);

        assertNotNull(dto);
        assertEquals(ecoNews.getId(), dto.getId());
        assertEquals(ecoNews.getTitle(), dto.getTitle());
        assertEquals(author.getName(), dto.getAuthor().getName());
        assertEquals(ecoNews.getCreationDate(), dto.getCreationDate());
        assertTrue(dto.getTags().isEmpty(), "Tags should be empty.");
    }

    @Test
    void testConvertFromEntityToDto_NullEcoNews(){
        EcoNews nullEco = null;
        assertThrows(NullPointerException.class, () -> searchNewsDtoMapper.convert(nullEco));
    }

    @Test
    void testConvertFromEntityToDto_NullTags(){
        EcoNews ecoNewsWithNullTags = ecoNews;
        ecoNewsWithNullTags.setTags(null);

        assertThrows(NullPointerException.class, () -> searchNewsDtoMapper.convert(ecoNewsWithNullTags));
    }

    @Test
    void testConvertFromEntityToDto_IncorrectLocale(){
        LocaleContextHolder.setLocale(Locale.CHINA);

        SearchNewsDto dto = searchNewsDtoMapper.convert(ecoNews);

        assertNotNull(dto);
        assertEquals(ecoNews.getId(), dto.getId());
        assertEquals(ecoNews.getTitle(), dto.getTitle());
        assertEquals(author.getName(), dto.getAuthor().getName());
        assertEquals(ecoNews.getCreationDate(), dto.getCreationDate());
        assertTrue(dto.getTags().isEmpty(), "Tags should be empty.");
    }
}
