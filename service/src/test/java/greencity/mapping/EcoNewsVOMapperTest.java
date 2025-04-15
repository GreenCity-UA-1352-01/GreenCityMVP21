package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EcoNewsVOMapperTest {
    private EcoNewsVOMapper mapper;

    @Mock
    private EcoNews ecoNews;

    @Mock
    private User author;

    @Mock
    private EcoNewsComment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new EcoNewsVOMapper();

        when(ecoNews.getAuthor()).thenReturn(author);
        when(ecoNews.getTags()).thenReturn(Collections.emptyList());
        when(ecoNews.getUsersLikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getUsersDislikedNews()).thenReturn(Collections.emptySet());
        when(ecoNews.getEcoNewsComments()).thenReturn(Collections.emptyList());
    }

    @Test
    void convertTest() {
        when(ecoNews.getId()).thenReturn(1L);
        when(ecoNews.getTitle()).thenReturn("Test Title");
        when(ecoNews.getText()).thenReturn("Test Text");
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Test Author");

        EcoNewsVO actual = mapper.convert(ecoNews);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Test Title", actual.getTitle());
        assertEquals("Test Text", actual.getText());

        assertNotNull(actual.getAuthor());
        assertEquals(1L, actual.getAuthor().getId());
        assertEquals("Test Author", actual.getAuthor().getName());

        assertNotNull(actual.getTags());
        assertTrue(actual.getTags().isEmpty());

        assertNotNull(actual.getUsersLikedNews());
        assertTrue(actual.getUsersLikedNews().isEmpty());

        assertNotNull(actual.getUsersDislikedNews());
        assertTrue(actual.getUsersDislikedNews().isEmpty());

        assertNotNull(actual.getEcoNewsComments());
        assertTrue(actual.getEcoNewsComments().isEmpty());
    }

    @Test
    void convertNullFieldsTest() {
        EcoNews emptyNews = new EcoNews();
        emptyNews.setTags(Collections.emptyList());
        emptyNews.setUsersLikedNews(Collections.emptySet());
        emptyNews.setUsersDislikedNews(Collections.emptySet());
        emptyNews.setEcoNewsComments(Collections.emptyList());
        User emptyAuthor = new User();
        emptyNews.setAuthor(emptyAuthor);

        EcoNewsVO actual = mapper.convert(emptyNews);

        assertNotNull(actual);
        assertNull(actual.getId());
        assertNull(actual.getTitle());
        assertNull(actual.getText());
        assertNull(actual.getCreationDate());
        assertNull(actual.getImagePath());
        assertNull(actual.getSource());

        assertNotNull(actual.getAuthor());
        assertNull(actual.getAuthor().getId());
        assertNull(actual.getAuthor().getName());
        assertNull(actual.getAuthor().getUserStatus());
        assertNull(actual.getAuthor().getRole());

        assertNotNull(actual.getTags());
        assertTrue(actual.getTags().isEmpty());
        assertNotNull(actual.getUsersLikedNews());
        assertTrue(actual.getUsersLikedNews().isEmpty());
        assertNotNull(actual.getUsersDislikedNews());
        assertTrue(actual.getUsersDislikedNews().isEmpty());
        assertNotNull(actual.getEcoNewsComments());
        assertTrue(actual.getEcoNewsComments().isEmpty());
    }

    @Test
    void convertWithCommentsTest() {
        User commentAuthor = new User();
        commentAuthor.setId(2L);
        commentAuthor.setName("Comment Author");

        EcoNewsComment comment = new EcoNewsComment();
        comment.setId(1L);
        comment.setUser(commentAuthor);
        comment.setText("Test Comment");

        when(ecoNews.getEcoNewsComments()).thenReturn(Collections.singletonList(comment));

        EcoNewsVO actual = mapper.convert(ecoNews);

        assertNotNull(actual.getEcoNewsComments());
        assertEquals(1, actual.getEcoNewsComments().size());
        var actualComment = actual.getEcoNewsComments().get(0);
        assertEquals(1L, actualComment.getId());
        assertEquals("Test Comment", actualComment.getText());
        assertEquals(2L, actualComment.getUser().getId());
        assertEquals("Comment Author", actualComment.getUser().getName());
    }
}
