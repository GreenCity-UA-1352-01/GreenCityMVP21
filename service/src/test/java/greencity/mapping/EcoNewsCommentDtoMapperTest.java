package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EcoNewsCommentDtoMapperTest {
    private EcoNewsCommentDtoMapper mapper;

    @Mock
    private EcoNewsComment comment;

    @Mock
    private User author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new EcoNewsCommentDtoMapper();

        when(comment.getUser()).thenReturn(author);
        when(comment.getUsersLiked()).thenReturn(new HashSet<>());
    }

    @Test
    void convertOriginalCommentTest() {
        LocalDateTime date = LocalDateTime.now();
        when(comment.getId()).thenReturn(1L);
        when(comment.getText()).thenReturn("Test comment");
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getCreatedDate()).thenReturn(date);
        when(comment.getModifiedDate()).thenReturn(date);
        when(comment.isCurrentUserLiked()).thenReturn(false);

        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Test author");
        when(author.getProfilePicturePath()).thenReturn("profile.jpg");

        EcoNewsCommentDto actual = mapper.convert(comment);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Test comment", actual.getText());
        assertEquals(CommentStatus.ORIGINAL, actual.getStatus());
        assertEquals(date, actual.getModifiedDate());
        assertFalse(actual.isCurrentUserLiked());
        assertEquals(0, actual.getLikes());

        EcoNewsCommentAuthorDto actualAuthor = actual.getAuthor();
        assertNotNull(actualAuthor);
        assertEquals(1L, actualAuthor.getId());
        assertEquals("Test author", actualAuthor.getName());
        assertEquals("profile.jpg", actualAuthor.getUserProfilePicturePath());
    }

    @Test
    void convertDeletedCommentTest() {
        when(comment.getId()).thenReturn(1L);
        when(comment.isDeleted()).thenReturn(true);
        when(comment.getModifiedDate()).thenReturn(LocalDateTime.now());

        EcoNewsCommentDto actual = mapper.convert(comment);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(CommentStatus.DELETED, actual.getStatus());
        assertNull(actual.getText());
        assertNull(actual.getAuthor());
    }

    @Test
    void convertEditedCommentTest() {
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime modifyDate = createDate.plusMinutes(5);

        when(comment.getId()).thenReturn(1L);
        when(comment.getText()).thenReturn("Edited comment");
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getCreatedDate()).thenReturn(createDate);
        when(comment.getModifiedDate()).thenReturn(modifyDate);

        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Test author");

        EcoNewsCommentDto actual = mapper.convert(comment);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Edited comment", actual.getText());
        assertEquals(CommentStatus.EDITED, actual.getStatus());
        assertEquals(modifyDate, actual.getModifiedDate());
    }
}
