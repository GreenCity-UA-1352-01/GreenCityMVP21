package greencity.validator;

import greencity.annotations.ValidComment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CommentValidator implements ConstraintValidator<ValidComment, String> {
    private static final Pattern URL_PATTERN = Pattern.compile("(?i)(https?://|www\\.)\\S+");
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
        "[\\p{InEMOTICONS}"
            + "\\p{InMISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS}"
            + "\\p{InTRANSPORT_AND_MAP_SYMBOLS}"
            + "\\p{InSUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS}"
            + "\\p{InSYMBOLS_AND_PICTOGRAPHS_EXTENDED_A}"
            + "\\p{InSYMBOLS_FOR_LEGACY_COMPUTING}"
            + "]"
    );

    @Override
    public boolean isValid(String comment, ConstraintValidatorContext context) {
        if (comment == null || comment.trim().isEmpty()) {
            return false;
        }

        return comment.length() <= 8000
            && !(URL_PATTERN.matcher(comment).find()
                || EMOJI_PATTERN.matcher(comment).find());
    }
}