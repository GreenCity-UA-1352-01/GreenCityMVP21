package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitManagementDto;
import greencity.entity.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HabitManagementDtoMapperTest {
    private final HabitManagementDtoMapper mapper = new HabitManagementDtoMapper();

    @Test
    void convertTest() {
        Language language = ModelUtils.getLanguage();
        Tag tag = ModelUtils.getTag();
        ShoppingListItem shoppingListItem = ModelUtils.getShoppingListItem();
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .language(language)
                .description("test")
                .habitItem("test")
                .name("test")
                .build();

        Habit habit = Habit.builder()
                .id(1L)
                .image("test")
                .defaultDuration(1)
                .complexity(1)
                .tags(Set.of(tag))
                .habitTranslations(List.of(habitTranslation))
                .shoppingListItems(Set.of(shoppingListItem))
                .build();

        HabitManagementDto result = mapper.convert(habit);

        assertNotNull(result);
        assertEquals(habit.getId(), result.getId());
        assertEquals(habit.getImage(), result.getImage());
        assertEquals(habit.getDefaultDuration(), result.getDefaultDuration());
        assertEquals(habit.getComplexity(), result.getComplexity());
    }

    @Test
    void testHandleNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((Habit) null));
    }

}
