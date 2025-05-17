package greencity.mapping;

import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EcoNewsCommentVOMapperTest {
    private EcoNewsCommentVOMapper mapper;

    @Mock
    private EcoNewsComment comment;

    @Mock
    private User author;

    @Mock
    private EcoNews ecoNews;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new EcoNewsCommentVOMapper();

        when(comment.getUser()).thenReturn(author);
        when(comment.getEcoNews()).thenReturn(ecoNews);
        when(comment.getUsersLiked()).thenReturn(new HashSet<>());
    }

    @Test
    void convertTest() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime modifiedDate = LocalDateTime.now();

        when(comment.getId()).thenReturn(1L);
        when(comment.getText()).thenReturn("Test Comment");
        when(comment.isDeleted()).thenReturn(false);
        when(comment.isCurrentUserLiked()).thenReturn(false);
        when(comment.getCreatedDate()).thenReturn(createDate);
        when(comment.getModifiedDate()).thenReturn(modifiedDate);
        when(comment.getParentComment()).thenReturn(null);

        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Test Author");
        when(author.getUserStatus()).thenReturn(UserStatus.ACTIVATED);
        when(author.getRole()).thenReturn(Role.ROLE_USER);

        when(ecoNews.getId()).thenReturn(1L);

        EcoNewsCommentVO actual = mapper.convert(comment);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Test Comment", actual.getText());
        assertFalse(actual.isDeleted());
        assertFalse(actual.isCurrentUserLiked());
        assertEquals(createDate, actual.getCreatedDate());
        assertEquals(modifiedDate, actual.getModifiedDate());
        assertNull(actual.getParentComment());

        assertNotNull(actual.getUser());
        assertEquals(1L, actual.getUser().getId());
        assertEquals("Test Author", actual.getUser().getName());
        assertEquals(Role.ROLE_USER, actual.getUser().getRole());

        assertNotNull(actual.getEcoNews());
        assertEquals(1L, actual.getEcoNews().getId());

        assertNotNull(actual.getUsersLiked());
        assertTrue(actual.getUsersLiked().isEmpty());
    }

    @Test
    void convertWithParentCommentTest() {
        // given
        EcoNewsComment parentComment = new EcoNewsComment();
        User parentAuthor = new User();
        parentAuthor.setId(2L);
        parentAuthor.setName("Parent Author");
        parentComment.setId(2L);
        parentComment.setText("Parent Comment");
        parentComment.setUser(parentAuthor);
        parentComment.setEcoNews(ecoNews);
        parentComment.setUsersLiked(new HashSet<>());

        when(comment.getParentComment()).thenReturn(parentComment);
        when(comment.getId()).thenReturn(1L);
        when(comment.getText()).thenReturn("Child Comment");
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Child Author");
        when(ecoNews.getId()).thenReturn(1L);

        EcoNewsCommentVO actual = mapper.convert(comment);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Child Comment", actual.getText());

        assertNotNull(actual.getParentComment());
        assertEquals(2L, actual.getParentComment().getId());
        assertEquals("Parent Comment", actual.getParentComment().getText());
        assertEquals(2L, actual.getParentComment().getUser().getId());
        assertEquals("Parent Author", actual.getParentComment().getUser().getName());
    }

    @Test
    void convertNullFieldsTest() {
        EcoNewsComment emptyComment = new EcoNewsComment();
        User emptyAuthor = new User();
        EcoNews emptyNews = new EcoNews();
        emptyComment.setUser(emptyAuthor);
        emptyComment.setEcoNews(emptyNews);
        emptyComment.setUsersLiked(new HashSet<>());

        EcoNewsCommentVO actual = mapper.convert(emptyComment);

        assertNotNull(actual);
        assertNull(actual.getId());
        assertNull(actual.getText());
        assertFalse(actual.isDeleted());
        assertFalse(actual.isCurrentUserLiked());
        assertNull(actual.getCreatedDate());
        assertNull(actual.getModifiedDate());
        assertNull(actual.getParentComment());

        assertNotNull(actual.getUser());
        assertNull(actual.getUser().getId());
        assertNull(actual.getUser().getName());
        assertNull(actual.getUser().getUserStatus());
        assertNull(actual.getUser().getRole());

        assertNotNull(actual.getEcoNews());
        assertNull(actual.getEcoNews().getId());

        assertNotNull(actual.getUsersLiked());
        assertTrue(actual.getUsersLiked().isEmpty());
    }
}