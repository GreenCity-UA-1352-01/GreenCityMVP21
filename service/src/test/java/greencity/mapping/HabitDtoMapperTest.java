package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitDto;
import greencity.entity.*;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class HabitDtoMapperTest {
    private final HabitDtoMapper mapper = new HabitDtoMapper();

    @Test
    void testConvertFromEntityToDto() {
        Language language = ModelUtils.getLanguage();
        Tag tag = ModelUtils.getTag();
        ShoppingListItem shoppingListItem = ModelUtils.getShoppingListItem();
        Habit habit = Habit.builder()
                .id(1L)
                .image("test")
                .defaultDuration(1)
                .complexity(1)
                .tags(Set.of(tag))
                .shoppingListItems(Set.of(shoppingListItem))
                .build();
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .habit(habit)
                .language(language)
                .description("test")
                .habitItem("test")
                .name("test")
                .build();

        HabitDto result = mapper.convert(habitTranslation);

        assertNotNull(result);
        assertEquals(habit.getId(), result.getId());
        assertEquals(habit.getImage(), result.getImage());
        assertEquals(habit.getDefaultDuration(), result.getDefaultDuration());
        assertEquals(habit.getComplexity(), result.getComplexity());
        assertEquals(habitTranslation.getDescription(), result.getHabitTranslation().getDescription());
        assertEquals(habitTranslation.getHabitItem(), result.getHabitTranslation().getHabitItem());
        assertEquals(habitTranslation.getName(), result.getHabitTranslation().getName());
        assertEquals(language.getCode(), result.getHabitTranslation().getLanguageCode());
        assertEquals(1, result.getTags().size());
        assertEquals(shoppingListItem.getId(), result.getShoppingListItems().get(0).getId());

    }

    @Test
    void testConvert_NullShoppingListItems() {
        Tag tag = ModelUtils.getTag();
        Habit habit = Habit.builder()
                .id(1L)
                .image("test")
                .defaultDuration(1)
                .complexity(1)
                .tags(Set.of(tag))
                .shoppingListItems(null)
                .build();
        Language language = ModelUtils.getLanguage();
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .habit(habit)
                .language(language)
                .description("test")
                .habitItem("test")
                .name("test")
                .build();

        HabitDto result = mapper.convert(habitTranslation);

        assertNotNull(result.getShoppingListItems());
        assertTrue(result.getShoppingListItems().isEmpty());
    }

    @Test
    void testConvert_NullInput_ReturnsNull() {
        assertThrows((NullPointerException.class), () -> mapper.convert((HabitTranslation) null));
    }
}
