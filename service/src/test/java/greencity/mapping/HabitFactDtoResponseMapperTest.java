package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class HabitFactDtoResponseMapperTest {
    private final HabitFactDtoResponseMapper mapper = new HabitFactDtoResponseMapper();

    @Test
    void testConvertFromEntityToDto() {
        Language language = ModelUtils.getLanguage();
        HabitVO habitVO = HabitVO.builder()
                .id(2L)
                .complexity(2)
                .image("test")
                .build();
        HabitFactTranslationVO translationVO = ModelUtils.getFactTranslationVO();
        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(1L)
                .habit(habitVO)
                .translations(List.of(translationVO))
                .build();

        HabitFactDtoResponse result = mapper.convert(habitFactVO);

        assertNotNull(result);
        assertEquals(habitFactVO.getId(), result.getId());
        assertEquals(habitVO.getId(), result.getHabit().getId());
        assertEquals(habitVO.getComplexity(), result.getHabit().getComplexity());
        assertEquals(habitVO.getImage(), result.getHabit().getImage());
        assertEquals(translationVO.getId(), result.getTranslations().get(0).getId());
        assertEquals(translationVO.getContent(), result.getTranslations().get(0).getContent());
        assertEquals(translationVO.getFactOfDayStatus(), result.getTranslations().get(0).getFactOfDayStatus());
        assertEquals(language.getId(), result.getTranslations().get(0).getLanguage().getId());

    }
    @Test
    void testHandleNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitFactVO) null));
    }
}
