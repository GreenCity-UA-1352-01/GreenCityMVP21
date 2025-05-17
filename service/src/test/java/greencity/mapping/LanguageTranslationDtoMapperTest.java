package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class LanguageTranslationDtoMapperTest {
    private final LanguageTranslationDtoMapper mapper = new LanguageTranslationDtoMapper();
    @Test
    void convertTest() {
        HabitFactTranslation habitFactTranslation = ModelUtils.getFactTranslation();
        LanguageTranslationDTO dto = mapper.convert(habitFactTranslation);

        assertNotNull(dto);
        assertEquals(habitFactTranslation.getContent(), dto.getContent());
        assertNotNull(dto.getLanguage());
        assertEquals(habitFactTranslation.getLanguage().getId(), dto.getLanguage().getId());
        assertEquals(habitFactTranslation.getLanguage().getCode(), dto.getLanguage().getCode());
    }

    @Test
    void convert_givenNull() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitFactTranslation) null));
    }

    @Test
    void convert_givenHabitFactTranslationWithNullLanguage() {
        HabitFactTranslation habitFactTranslation = ModelUtils.getFactTranslation();
        habitFactTranslation.setLanguage(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(habitFactTranslation));
    }
}
