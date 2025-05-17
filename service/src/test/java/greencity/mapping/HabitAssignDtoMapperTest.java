package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class HabitAssignDtoMapperTest {

    private final HabitAssignDtoMapper mapper = new HabitAssignDtoMapper();
    @Test
    void convertTest() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        HabitAssignDto dto = mapper.convert(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getUser().getId(), dto.getUserId());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getCreateDate(), dto.getCreateDateTime());
        assertEquals(entity.getDuration(), dto.getDuration());
        assertEquals(entity.getHabitStreak(), dto.getHabitStreak());
        assertEquals(entity.getWorkingDays(), dto.getWorkingDays());
        assertEquals(entity.getLastEnrollmentDate(), dto.getLastEnrollmentDate());

        List<HabitStatusCalendarDto> calendarDtos = dto.getHabitStatusCalendarDtoList();
        assertEquals(1, calendarDtos.size());

        HabitStatusCalendarDto calendarDto = calendarDtos.get(0);
        HabitStatusCalendar calendarEntity = entity.getHabitStatusCalendars().get(0);
        assertEquals(calendarEntity.getId(), calendarDto.getId());
        assertEquals(calendarEntity.getEnrollDate(), calendarDto.getEnrollDate());
    }

    @Test
    void convertTest_givenNullInput() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitAssign) null));
    }

    @Test
    void convert_givenNullUser() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        entity.setUser(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @Test
    void convert_givenNullHabitStatusCalendars() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        entity.setHabitStatusCalendars(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(entity));
    }

    @Test
    void convert_givenEmptyHabitStatusCalendars() {
        HabitAssign entity = ModelUtils.getHabitAssign();
        entity.setHabitStatusCalendars(Collections.emptyList());

        HabitAssignDto dto = mapper.convert(entity);

        assertNotNull(dto);
        assertNotNull(dto.getHabitStatusCalendarDtoList());
        assertEquals(0, dto.getHabitStatusCalendarDtoList().size());
    }
}
