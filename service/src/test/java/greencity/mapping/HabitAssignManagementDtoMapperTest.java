package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class HabitAssignManagementDtoMapperTest {

    private final HabitAssignManagementDtoMapper mapper = new HabitAssignManagementDtoMapper();

    @Test
    void convertTest() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        HabitAssignManagementDto dto = mapper.convert(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getCreateDate(), dto.getCreateDateTime());
        assertEquals(entity.getUser().getId(), dto.getUserId());
        assertEquals(entity.getHabit().getId(), dto.getHabitId());
        assertEquals(entity.getDuration(), dto.getDuration());
        assertEquals(entity.getHabitStreak(), dto.getHabitStreak());
        assertEquals(entity.getWorkingDays(), dto.getWorkingDays());
        assertEquals(entity.getLastEnrollmentDate(), dto.getLastEnrollment());
    }

    @Test
    void convert_givenNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitAssign) null));
    }

    @Test
    void convert_givenNullHabit() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        entity.setHabit(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @Test
    void convert_givenNullUser() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        entity.setUser(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @Test
    void convert_givenHabitAssignWithNullHabit() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        entity.setHabit(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }
}
