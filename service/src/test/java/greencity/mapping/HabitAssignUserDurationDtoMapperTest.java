package greencity.mapping;

import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import greencity.ModelUtils;

import static org.junit.jupiter.api.Assertions.*;

public class HabitAssignUserDurationDtoMapperTest {
    private final HabitAssignUserDurationDtoMapper mapper = new HabitAssignUserDurationDtoMapper();
    @Test
    void convertTest() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        HabitAssignUserDurationDto dto = mapper.convert(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getHabitAssignId());
        assertEquals(entity.getUser().getId(), dto.getUserId());
        assertEquals(entity.getHabit().getId(), dto.getHabitId());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getWorkingDays(), dto.getWorkingDays());
        assertEquals(entity.getDuration(), dto.getDuration());
    }

    @Test
    void convert_givenNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitAssign) null));
    }
}
