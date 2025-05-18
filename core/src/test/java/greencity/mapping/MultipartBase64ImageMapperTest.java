package greencity.mapping;

import greencity.exception.exceptions.NotSavedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class MultipartBase64ImageMapperTest {

    private MultipartBase64ImageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MultipartBase64ImageMapper();
    }

    @Test
    void convert_validBase64Image_shouldReturnMultipartFile() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        String base64Image = Base64.getEncoder().encodeToString(os.toByteArray());

        String base64Input = "data:image/png;base64," + base64Image;

        MultipartFile multipartFile = mapper.convert(base64Input);

        assertNotNull(multipartFile);
        assertEquals("mainFile", multipartFile.getName());
        assertEquals("tempImage.jpg", multipartFile.getOriginalFilename());
        assertTrue(multipartFile.getSize() > 0);
        assertEquals("image/jpeg", multipartFile.getContentType());
    }

    @Test
    void convert_invalidBase64Image_shouldThrowNotSavedException() {
        String invalidBase64 = "data:image/png;base64,thisIsNotBase64!";

        assertThrows(NotSavedException.class, () -> mapper.convert(invalidBase64));
    }

    @Test
    void convert_nullOrEmpty_shouldThrowException() {
        assertThrows(NotSavedException.class, () -> mapper.convert(""));
        assertThrows(NullPointerException.class, () -> mapper.convert((String) null));
    }

    @Test
    void convert_base64WithoutPrefix_shouldThrowNotSavedException() {
        String base64 = "notAValidBase64Image";
        assertThrows(NotSavedException.class, () -> mapper.convert(base64));
    }
}
