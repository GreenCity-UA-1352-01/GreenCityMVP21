package greencity.validator;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    private AddEcoNewsDtoRequest request;

    @BeforeEach
    void setUp() {
        request = new AddEcoNewsDtoRequest();
    }

    @Test
    void isValidWithValidDataTest() {
        request.setTags(Arrays.asList("tag1", "tag2"));
        request.setSource("https://example.com");

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValidWithNullSourceTest() {
        request.setTags(Arrays.asList("tag1", "tag2"));
        request.setSource(null);

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValidWithEmptySourceTest() {
        request.setTags(Arrays.asList("tag1", "tag2"));
        request.setSource("");

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void throwsExceptionWhenTagsEmptyTest() {
        request.setTags(Collections.emptyList());
        request.setSource("https://example.com");

        Exception exception = assertThrows(WrongCountOfTagsException.class,
                () -> validator.isValid(request, context));

        assertEquals(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION, exception.getMessage());
    }

    @Test
    void throwsExceptionWhenTooManyTagsTest() {
        request.setTags(Arrays.asList("tag1", "tag2", "tag3", "tag4"));
        request.setSource("https://example.com");

        Exception exception = assertThrows(WrongCountOfTagsException.class,
                () -> validator.isValid(request, context));

        assertEquals(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION, exception.getMessage());
    }

    @Test
    void throwsExceptionWhenInvalidSourceUrlTest() {
        request.setTags(Arrays.asList("tag1", "tag2"));
        request.setSource("invalid-url");

        assertThrows(InvalidURLException.class,
                () -> validator.isValid(request, context));
    }
}
