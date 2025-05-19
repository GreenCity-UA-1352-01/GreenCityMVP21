package greencity.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommentValidatorTest {

    private static final CommentValidator VALIDATOR;

    static {
        VALIDATOR = new CommentValidator();
    }

    @Test
    void testValid_whenNull_shouldReturnFalse() {
        boolean actual = VALIDATOR.isValid(null, null);
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testValid_whenEmpty_shouldReturnFalse(String comment) {
        boolean actual = VALIDATOR.isValid(comment, null);
        assertFalse(actual);
    }

    @Test
    void testValid_whenLengthOver8K_shouldReturnFalse() {
        String comment = "a".repeat(8001);
        boolean actual = VALIDATOR.isValid(comment, null);
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1https://google.com1", "1http://google.com1", "1HTTPS://GOOGLE.COM/1",
        "1www.google.com1"})
    void testValid_whenContainsLink_shouldReturnFalse(String comment) {
        boolean actual = VALIDATOR.isValid(comment, null);
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1\uD83D\uDE00 \uD83D\uDE01 \uD83D\uDE021", "1\uD83D\uDE80 \uD83D\uDE81 \uD83D\uDE821"})
    void testValid_whenContainsEmoji_shouldReturnFalse(String comment) {
        boolean actual = VALIDATOR.isValid(comment, null);
        assertFalse(actual);
    }

    @Test
    void testValid_whenValid_shouldReturnTrue() {
        String comment = "comment";
        boolean actual = VALIDATOR.isValid(comment, null);
        assertTrue(actual);
    }

}
