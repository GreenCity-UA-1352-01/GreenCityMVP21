package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HabitStatusCalendarMapperTest {
    private HabitStatusCalendarMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HabitStatusCalendarMapper();
    }

    @Test
    void convert_shouldMapAllFieldsCorrectly() {
        LocalDate now = LocalDate.now();
        HabitAssignVO habitAssignVO = HabitAssignVO.builder()
                .id(42L)
                .build();

        HabitStatusCalendarVO vo = HabitStatusCalendarVO.builder()
                .id(1L)
                .enrollDate(now)
                .habitAssignVO(habitAssignVO)
                .build();

        HabitStatusCalendar entity = mapper.convert(vo);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(now, entity.getEnrollDate());
        assertNotNull(entity.getHabitAssign());
        assertEquals(42L, entity.getHabitAssign().getId());
    }

    @Test
    void convert_givenNullInput_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitStatusCalendarVO) null));
    }

    @Test
    void convert_givenNullNestedHabitAssignVO_shouldThrowNullPointerException() {
        HabitStatusCalendarVO vo = HabitStatusCalendarVO.builder()
                .id(2L)
                .enrollDate(LocalDate.now())
                .habitAssignVO(null)
                .build();

        assertThrows(NullPointerException.class, () -> mapper.convert(vo));
    }

    @Test
    void convert_shouldReturnEntityEqualToExpected_whenAllFieldsSet() {
        LocalDate now = LocalDate.now();

        HabitAssignVO habitAssignVO = HabitAssignVO.builder()
                .id(100L)
                .build();

        HabitStatusCalendarVO vo = HabitStatusCalendarVO.builder()
                .id(999L)
                .enrollDate(now)
                .habitAssignVO(habitAssignVO)
                .build();

        HabitStatusCalendar expected = HabitStatusCalendar.builder()
                .id(999L)
                .enrollDate(now)
                .habitAssign(HabitAssign.builder().id(100L).build())
                .build();

        HabitStatusCalendar actual = mapper.convert(vo);

        assertEquals(expected, actual);
    }
}
