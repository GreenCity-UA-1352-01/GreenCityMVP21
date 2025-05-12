package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.HabitController;
import greencity.dto.habit.HabitDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.NotificationEventFactory;
import greencity.service.HabitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@AllArgsConstructor
@NotificationHandler
public class HabitLikeNotificationFactory implements NotificationEventFactory {
    private final HabitService habitService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class};
        return method.getDeclaringClass().equals(HabitController.class)
                && method.getName().equals("likeHabit")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long habitId = (Long) args[0];
        UserVO user = (UserVO) args[1];
        HabitDto habitDto = habitService.getHabitById(habitId);
        System.out.println(habitDto);

        return NotificationRequestDto.builder()
                .action("likes")
                .objectName("Habit")
                .objectLink("/habit/" + habitId)
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(habitDto.getUsersIdWhoCreatedCustomHabit())
                .initiatorId(user.getId())
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
