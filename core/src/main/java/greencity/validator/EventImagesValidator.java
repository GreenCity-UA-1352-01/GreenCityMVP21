package greencity.validator;

import greencity.annotations.ValidEventImages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public class EventImagesValidator implements ConstraintValidator<ValidEventImages, List<MultipartFile>> {
    private static final int MAX_FILES = 5;
    private static final int MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true;
        }

        if (files.size() > MAX_FILES) {
            return false;
        }

        for (MultipartFile file : files) {
            if (file == null || file.getSize() > MAX_SIZE_BYTES) {
                return false;
            }

            String contentType = file.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                return false;
            }
        }
        return true;
    }
}
