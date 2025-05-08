package greencity.mapping;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EcoNewsDtoMapperTest {
    private EcoNewsDtoMapper mapper;

    @Mock
    private EcoNews ecoNews;

    @Mock
    private User author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new EcoNewsDtoMapper();

        when(ecoNews.getAuthor()).thenReturn(author);
        when(ecoNews.getTags()).thenReturn(Collections.emptyList());
        when(ecoNews.getUsersLikedNews()).thenReturn(new HashSet<>());
        when(ecoNews.getUsersDislikedNews()).thenReturn(new HashSet<>());
        when(ecoNews.getEcoNewsComments()).thenReturn(Collections.emptyList());
    }

    @Test
    void convertTest() {
        ZonedDateTime creationDate = ZonedDateTime.now();
        when(ecoNews.getId()).thenReturn(1L);
        when(ecoNews.getText()).thenReturn("Test Content");
        when(ecoNews.getTitle()).thenReturn("Test Title");
        when(ecoNews.getCreationDate()).thenReturn(creationDate);
        when(ecoNews.getImagePath()).thenReturn("image.jpg");
        when(ecoNews.getShortInfo()).thenReturn("Short Info");
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Test Author");

        EcoNewsDto actual = mapper.convert(ecoNews);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Test Content", actual.getContent());
        assertEquals("Test Title", actual.getTitle());
        assertEquals(creationDate, actual.getCreationDate());
        assertEquals("image.jpg", actual.getImagePath());
        assertEquals("Short Info", actual.getShortInfo());
        assertEquals(0, actual.getLikes());
        assertEquals(0, actual.getDislikes());
        assertEquals(0, actual.getCountComments());

        assertNotNull(actual.getAuthor());
        assertEquals(1L, actual.getAuthor().getId());
        assertEquals("Test Author", actual.getAuthor().getName());

        assertNotNull(actual.getTags());
        assertTrue(actual.getTags().isEmpty());
        assertNotNull(actual.getTagsUa());
        assertTrue(actual.getTagsUa().isEmpty());
    }

    @Test
    void convertWithTagsTest() {
        Language langEn = new Language();
        langEn.setCode(AppConstant.DEFAULT_LANGUAGE_CODE);
        Language langUa = new Language();
        langUa.setCode("ua");

        TagTranslation translationEn = new TagTranslation();
        translationEn.setLanguage(langEn);
        translationEn.setName("News");

        TagTranslation translationUa = new TagTranslation();
        translationUa.setLanguage(langUa);
        translationUa.setName("Новини");

        Tag tag = new Tag();
        tag.setTagTranslations(Arrays.asList(translationEn, translationUa));

        when(ecoNews.getTags()).thenReturn(Collections.singletonList(tag));

        EcoNewsDto actual = mapper.convert(ecoNews);

        assertNotNull(actual.getTags());
        assertEquals(1, actual.getTags().size());
        assertEquals("News", actual.getTags().get(0));

        assertNotNull(actual.getTagsUa());
        assertEquals(1, actual.getTagsUa().size());
        assertEquals("Новини", actual.getTagsUa().get(0));
    }

    @Test
    void convertWithNullFieldsTest() {
        EcoNews emptyNews = new EcoNews();
        User emptyAuthor = new User();
        emptyNews.setAuthor(emptyAuthor);
        emptyNews.setTags(Collections.emptyList());
        emptyNews.setUsersLikedNews(new HashSet<>());
        emptyNews.setUsersDislikedNews(new HashSet<>());
        emptyNews.setEcoNewsComments(Collections.emptyList());

        EcoNewsDto actual = mapper.convert(emptyNews);

        assertNotNull(actual);
        assertNull(actual.getId());
        assertNull(actual.getContent());
        assertNull(actual.getTitle());
        assertNull(actual.getCreationDate());
        assertNull(actual.getImagePath());
        assertNull(actual.getShortInfo());
        assertEquals(0, actual.getLikes());
        assertEquals(0, actual.getDislikes());
        assertEquals(0, actual.getCountComments());

        assertNotNull(actual.getAuthor());
        assertNull(actual.getAuthor().getId());
        assertNull(actual.getAuthor().getName());

        assertNotNull(actual.getTags());
        assertTrue(actual.getTags().isEmpty());
        assertNotNull(actual.getTagsUa());
        assertTrue(actual.getTagsUa().isEmpty());
    }
}
