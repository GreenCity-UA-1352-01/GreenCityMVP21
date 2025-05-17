package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EcoNewsAuthorDtoMapperTest {
    private EcoNewsAuthorDtoMapper mapper;

    @Mock
    private User author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new EcoNewsAuthorDtoMapper();
    }

    @Test
    void convertTest() {
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Test Author");

        EcoNewsAuthorDto actual = mapper.convert(author);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("Test Author", actual.getName());
    }

    @Test
    void convertNullFieldsTest() {
        User emptyAuthor = new User();

        EcoNewsAuthorDto actual = mapper.convert(emptyAuthor);

        assertNotNull(actual);
        assertNull(actual.getId());
        assertNull(actual.getName());
    }
}
