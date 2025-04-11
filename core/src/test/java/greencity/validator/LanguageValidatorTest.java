package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageValidatorTest {
    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageValidator languageValidator;

    @Test
    void isValidTrueTest() {
        List<String> trueCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(trueCodes);
        languageValidator.initialize(null);

        boolean result = languageValidator.isValid(new Locale("en"), null);
        assertTrue(result);
    }

    @Test
    void isValidFalseTest() {
        List<String> trueCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(trueCodes);
        languageValidator.initialize(null);

        boolean result = languageValidator.isValid(new Locale("fr"), null);
        assertFalse(result);
    }

    @Test
    void isValidNullLanguageTest() {
        List<String> trueCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(trueCodes);
        languageValidator.initialize(null);

        Locale nullLocale = new Locale("");
        boolean result = languageValidator.isValid(nullLocale, null);
        assertFalse(result);
    }

    @Test
    void isValidEmptyLanguageTest() {
        List<String> trueCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(trueCodes);
        languageValidator.initialize(null);

        boolean result = languageValidator.isValid(new Locale(""), null);
        assertFalse(result);
    }

    @Test
    void initializeTest() {
        List<String> trueCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(trueCodes);
        languageValidator.initialize(null);

        boolean result = languageValidator.isValid(new Locale("ua"), null);
        assertTrue(result);
    }
}
