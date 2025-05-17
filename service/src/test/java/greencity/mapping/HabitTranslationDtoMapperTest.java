package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HabitTranslationDtoMapperTest {
    private final HabitTranslationDtoMapper mapper = new HabitTranslationDtoMapper();

    @Test
    void testConvertFromEntityToDto() {
        Language language = ModelUtils.getLanguage();
        Habit habit = Habit.builder()
                .id(1L)
                .image("Test Image")
                .defaultDuration(1)
                .complexity(1)
                .build();
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("Test Name")
                .habit(habit)
                .language(language)
                .description("Test Description")
                .habitItem("Test Habit Item")
                .build();

        HabitTranslationDto result = mapper.convert(habitTranslation);

        assertNotNull(result);
        assertEquals(habitTranslation.getDescription(), result.getDescription());
        assertEquals(habitTranslation.getHabitItem(), result.getHabitItem());
        assertEquals(habitTranslation.getLanguage().getCode(), result.getLanguageCode());
        assertEquals(habitTranslation.getName(), result.getName());

    }
    @Test
    void testMapAllToList(){
        HabitTranslation habitTranslation1 = HabitTranslation.builder().
                id(1L)
                .name("Test Name 1")
                .description("Test Description 1")
                .habitItem("Test Habit Item 1")
                .language(ModelUtils.getLanguage())
                .build();
        HabitTranslation habitTranslation2 = HabitTranslation.builder()
                .id(2L)
                .name("Test Name 2")
                .description("Test Description 2")
                .habitItem("Test Habit Item 2")
                .language(ModelUtils.getLanguage())
                .build();
        List<HabitTranslation> list = List.of(habitTranslation1, habitTranslation2);

        List<HabitTranslationDto> result = mapper.mapAllToList(list);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(habitTranslation1.getDescription(), result.get(0).getDescription());
        assertEquals(habitTranslation1.getHabitItem(), result.get(0).getHabitItem());
        assertEquals(habitTranslation1.getLanguage().getCode(), result.get(0).getLanguageCode());
        assertEquals(habitTranslation1.getName(), result.get(0).getName());
        assertEquals(habitTranslation2.getDescription(), result.get(1).getDescription());
        assertEquals(habitTranslation2.getHabitItem(), result.get(1).getHabitItem());
        assertEquals(habitTranslation2.getLanguage().getCode(), result.get(1).getLanguageCode());
        assertEquals(habitTranslation2.getName(), result.get(1).getName());

    }
    @Test
    void testHandleNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitTranslation) null));
    }
}
