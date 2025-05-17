package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.HabitController;
import greencity.dto.habit.HabitDto;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationType;
import greencity.notification.NotificationDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.HabitService;
import java.util.Set;
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

        ZonedDateTime creationDate = ZonedDateTime.now();
        String habitName = habitDto.getHabitTranslation().getName();
        String title = habitName.length() > 20
            ? habitName.substring(0, 17) + "..."
            : habitName;
        String action = "%s liked your habit %s. %s".formatted(user.getName(), title,
            NotificationDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectId(habitId)
                .objectName(title)
                .creationDate(ZonedDateTime.now())
                .receiverIds(Set.of(habitDto.getUsersIdWhoCreatedCustomHabit()))
                .initiatorId(user.getId())
                .objectType(NotificationObjectType.HABIT)
                .notificationType(NotificationType.HABIT_LIKE)
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
