package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.HabitAssign;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class HabitAssignMapperTest {

    private final HabitAssignMapper mapper = new HabitAssignMapper();

    @Test
    void convertTest() {
        HabitAssignDto dto = HabitAssignDto.builder()
                .id(1L)
                .duration(30)
                .habitStreak(5)
                .createDateTime(ZonedDateTime.now())
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(7)
                .lastEnrollmentDate(ZonedDateTime.now().minusDays(1))
                .progressNotificationHasDisplayed(true)
                .habit(HabitDto.builder().id(10L).complexity(2).build())
                .userShoppingListItems(List.of(
                        UserShoppingListItemAdvanceDto.builder()
                                .id(1L)
                                .status(ShoppingListItemStatus.INPROGRESS)
                                .shoppingListItemId(21L)
                                .dateCompleted(LocalDateTime.now())
                                .build(),
                        UserShoppingListItemAdvanceDto.builder()
                                .id(11L)
                                .status(ShoppingListItemStatus.DONE)
                                .shoppingListItemId(10L)
                                .build()
                ))
                .build();

        HabitAssign entity = mapper.convert(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getDuration(), entity.getDuration());
        assertEquals(dto.getHabitStreak(), entity.getHabitStreak());
        assertEquals(dto.getCreateDateTime(), entity.getCreateDate());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getWorkingDays(), entity.getWorkingDays());
        assertEquals(dto.getLastEnrollmentDate(), entity.getLastEnrollmentDate());

        assertNotNull(entity.getHabit());
        assertEquals(dto.getHabit().getId(), entity.getHabit().getId());
        assertEquals(dto.getHabit().getComplexity(), entity.getHabit().getComplexity());

        assertNotNull(entity.getUserShoppingListItems());
        assertEquals(1, entity.getUserShoppingListItems().size());
    }

    @Test
    void convert_givenNullDto_() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitAssignDto) null));
    }

    @Test
    void convert_givenNullHabit() {
        HabitAssignDto dto = HabitAssignDto.builder()
                .id(1L)
                .duration(30)
                .habitStreak(5)
                .createDateTime(ZonedDateTime.now())
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(7)
                .lastEnrollmentDate(ZonedDateTime.now())
                .progressNotificationHasDisplayed(false)
                .habit(null)
                .userShoppingListItems(List.of())
                .build();

        assertThrows(NullPointerException.class, () -> mapper.convert(dto));
    }

    @Test
    void convert_givenEmptyShoppingList() {
        HabitAssignDto dto = HabitAssignDto.builder()
                .id(1L)
                .duration(30)
                .habitStreak(5)
                .createDateTime(ZonedDateTime.now())
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(7)
                .lastEnrollmentDate(ZonedDateTime.now())
                .progressNotificationHasDisplayed(true)
                .habit(HabitDto.builder().id(10L).complexity(2).build())
                .userShoppingListItems(List.of())
                .build();

        HabitAssign result = mapper.convert(dto);

        assertNotNull(result);
        assertTrue(result.getUserShoppingListItems().isEmpty());
    }
}

