package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HabitTranslationMapperTest {
    private final HabitTranslationMapper mapper = new HabitTranslationMapper();
    @Test
    void testConvertFromEntityToDto() {
        Language language = ModelUtils.getLanguage();
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .habitItem("Test Habit Item")
                .description("Test Description")
                .languageCode(language.getCode())
                .name("Test Name")
                .build();

        HabitTranslation result = mapper.convert(habitTranslationDto);

        assertNotNull(result);
        assertEquals(habitTranslationDto.getHabitItem(), result.getHabitItem());
        assertEquals(habitTranslationDto.getDescription(), result.getDescription());
        assertEquals(habitTranslationDto.getName(), result.getName());
    }

    @Test
    void testMapAllToList() {
        Language language = ModelUtils.getLanguage();
        HabitTranslationDto habitTranslationDto1 = HabitTranslationDto.builder()
                .habitItem("Test Habit Item1")
                .description("Test Description1")
                .languageCode(language.getCode())
                .name("Test Name1")
                .build();
        HabitTranslationDto habitTranslationDto2 = HabitTranslationDto.builder()
                .habitItem("Test Habit Item2")
                .description("Test Description2")
                .languageCode(language.getCode())
                .name("Test Name2")
                .build();
        List<HabitTranslationDto> list = List.of(habitTranslationDto1, habitTranslationDto2);

        List<HabitTranslation> result = mapper.mapAllToList(list);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(habitTranslationDto1.getHabitItem(), result.get(0).getHabitItem());
        assertEquals(habitTranslationDto1.getDescription(), result.get(0).getDescription());
        assertEquals(habitTranslationDto1.getName(), result.get(0).getName());
        assertEquals(habitTranslationDto2.getHabitItem(), result.get(1).getHabitItem());
        assertEquals(habitTranslationDto2.getDescription(), result.get(1).getDescription());
        assertEquals(habitTranslationDto2.getName(), result.get(1).getName());
    }

    @Test
    void testHandleNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitTranslationDto) null));
    }
}
