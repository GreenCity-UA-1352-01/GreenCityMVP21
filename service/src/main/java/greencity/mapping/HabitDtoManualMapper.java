package greencity.mapping;

import greencity.dto.habit.HabitDto;
import greencity.entity.Habit;
import org.springframework.stereotype.Component;

@Component
public class HabitDtoManualMapper {
    public HabitDto toDto(Habit habit) {
        return HabitDto.builder()
                .id(habit.getId())
                .usersIdWhoCreatedCustomHabit(habit.getUserId())
                .image(habit.getImage())
                .defaultDuration(habit.getDefaultDuration())
                .complexity(habit.getComplexity())
                .isCustomHabit(habit.getIsCustomHabit())
                .build();
    }
}

