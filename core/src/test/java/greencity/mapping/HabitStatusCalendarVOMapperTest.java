package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HabitStatusCalendarVOMapperTest {

    private HabitStatusCalendarVOMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HabitStatusCalendarVOMapper();
    }

    @Test
    void convert_shouldMapAllFieldsCorrectly() {
        LocalDate now = LocalDate.now();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(77L)
                .build();

        HabitStatusCalendar entity = HabitStatusCalendar.builder()
                .id(10L)
                .enrollDate(now)
                .habitAssign(habitAssign)
                .build();

        HabitStatusCalendarVO vo = mapper.convert(entity);

        assertNotNull(vo);
        assertEquals(10L, vo.getId());
        assertEquals(now, vo.getEnrollDate());
        assertNotNull(vo.getHabitAssignVO());
        assertEquals(77L, vo.getHabitAssignVO().getId());
    }

    @Test
    void convert_givenNullInput_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitStatusCalendar) null));
    }

    @Test
    void convert_givenNullHabitAssign_shouldThrowNullPointerException() {
        HabitStatusCalendar entity = HabitStatusCalendar.builder()
                .id(11L)
                .enrollDate(LocalDate.now())
                .habitAssign(null)
                .build();

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @Test
    void convert_shouldReturnVOEqualToExpected_whenAllFieldsSet() {
        LocalDate now = LocalDate.now();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(123L)
                .build();

        HabitStatusCalendar entity = HabitStatusCalendar.builder()
                .id(888L)
                .enrollDate(now)
                .habitAssign(habitAssign)
                .build();

        HabitStatusCalendarVO expected = HabitStatusCalendarVO.builder()
                .id(888L)
                .enrollDate(now)
                .habitAssignVO(HabitAssignVO.builder().id(123L).build())
                .build();

        HabitStatusCalendarVO actual = mapper.convert(entity);

        assertEquals(expected, actual);
    }
}
