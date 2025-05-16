package greencity.validator;

import greencity.annotations.ImageValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ImageValidatorTest {
    private ImageValidator validator;
    private MultipartFile multipartFile;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ImageValidator();
        multipartFile = mock(MultipartFile.class);
        context = mock(ConstraintValidatorContext.class);
        validator.initialize(null);
    }

    @Test
    void testIsValid_nullFile_returnsTrue() {

        MultipartFile nullFile = null;

        boolean result = validator.isValid(nullFile, context);

        assertTrue(result);
    }

    @Test
    void testIsValid_validJpeg_returnsTrue() {

        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        boolean result = validator.isValid(multipartFile, context);

        assertTrue(result);
    }

    @Test
    void testIsValid_validPng_returnsTrue() {

        when(multipartFile.getContentType()).thenReturn("image/png");

        boolean result = validator.isValid(multipartFile, context);

        assertTrue(result);
    }

    @Test
    void testIsValid_validJpg_returnsTrue() {

        when(multipartFile.getContentType()).thenReturn("image/jpg");

        boolean result = validator.isValid(multipartFile, context);

        assertTrue(result);
    }

    @Test
    void testIsValid_invalidGif_returnsFalse() {

        when(multipartFile.getContentType()).thenReturn("image/gif");

        boolean result = validator.isValid(multipartFile, context);

        assertFalse(result);
    }

    @Test
    void testIsValid_invalidPdf_returnsFalse() {

        when(multipartFile.getContentType()).thenReturn("application/pdf");

        boolean result = validator.isValid(multipartFile, context);

        assertFalse(result);
    }

    @Test
    void testIsValid_nullContentType_returnsFalse() {
        when(multipartFile.getContentType()).thenReturn(null);

        boolean result = validator.isValid(multipartFile, context);

        assertFalse(result);
    }
}
