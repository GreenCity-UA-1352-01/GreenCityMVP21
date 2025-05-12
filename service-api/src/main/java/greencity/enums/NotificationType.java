package greencity.enums;

import greencity.dto.notification.*;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    HABIT_COMMENT("%s commented on your habit %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage),
    HABIT_INVITATION("%s invited you to habit %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage),
    HABIT_LIKE("%s liked your habit %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage),
    EVENT_COMMENT("%s commented on your event %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage),
    EVENT_LIKE("%s liked your event %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage),
    EVENT_UPDATE_NAME("Event %s was updated. New name is %s. %s", 1, false,
        SingleMessageConverter::actionMessage, GroupedMessageConverter::actionMessage),
    EVENT_UPDATE_DATETIME("Date and time for event %s was updated. New date and time is %s. %s", 1, false,
        SingleMessageConverter::actionMessage, GroupedMessageConverter::actionMessage),
    EVENT_UPDATE_LOCATION("Location for event %s was updated. New location is %s. %s", 1, false,
        SingleMessageConverter::actionMessage, GroupedMessageConverter::actionMessage),
    EVENT_CANCEL("Unfortunately, event %s was cancelled. %s", 1, false,
        SingleMessageConverter::actionMessage, GroupedMessageConverter::actionMessage),
    FRIENDSHIP_REQUEST_ACCEPT("%s accepted your friend request. %s", 1, false,
        SingleMessageConverter::actionMessage, GroupedMessageConverter::actionMessage),
    FRIENDSHIP_REQUEST("%s sent you a friend request. %s", 1, false,
        SingleMessageConverter::actionMessage, GroupedMessageConverter::actionMessage),
    COMMENT_LIKE("%s liked your comment. %s", 2, true,
        SingleMessageConverter::noObjectNameMessage, GroupedMessageConverter::noObjectNameMessage),
    COMMENT_REPLY("%s replied to your comment on the %s %s. %s", 2, true,
        SingleMessageConverter::objectNameAndTypeMessage, GroupedMessageConverter::objectNameAndTypeMessage),
    ECO_NEWS_LIKE("%s likes your news %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage),
    ECO_NEWS_COMMENT("%s commented on your news %s. %s", 2, true,
        SingleMessageConverter::objectNameMessage, GroupedMessageConverter::objectNameMessage);

    private final String messageTemplate;
    private final int shownInitiatorsCount;
    private final boolean isGroupable;
    private final Function<NotificationResponseDto, String> singleMessageConverter;
    private final Function<NotificationsGroupedResponseDto, String> groupedMessageConverter;

    private static class SingleMessageConverter {
        public static String objectNameMessage(NotificationResponseDto notification) {
            return String.format(notification.getNotificationType().getMessageTemplate(),
                notification.getInitiatorName(),
                notification.getObjectName(),
                NotificationDateTimeFormatter.format(notification.getCreationDate()));
        }

        public static String objectNameAndTypeMessage(NotificationResponseDto notification) {
            return String.format(notification.getNotificationType().getMessageTemplate(),
                notification.getInitiatorName(),
                notification.getObjectType().name().toLowerCase(),
                notification.getObjectName(),
                NotificationDateTimeFormatter.format(notification.getCreationDate()));
        }

        public static String noObjectNameMessage(NotificationResponseDto notification) {
            return String.format(notification.getNotificationType().getMessageTemplate(),
                notification.getInitiatorName(),
                NotificationDateTimeFormatter.format(notification.getCreationDate()));
        }

        public static String actionMessage(NotificationResponseDto notification) {
            return String.format(notification.getNotificationType().getMessageTemplate(),
                notification.getAction());
        }
    }

    private static class GroupedMessageConverter {
        public static String objectNameMessage(NotificationsGroupedResponseDto notificationsGroup) {
            return String.format(notificationsGroup.getNotificationType().getMessageTemplate(),
                formatInitiators(notificationsGroup),
                notificationsGroup.getObjectName(),
                NotificationDateTimeFormatter.format(notificationsGroup.getCreationDate()));
        }

        public static String objectNameAndTypeMessage(NotificationsGroupedResponseDto notificationsGroup) {
            return String.format(notificationsGroup.getNotificationType().getMessageTemplate(),
                formatInitiators(notificationsGroup),
                notificationsGroup.getObjectType().name().toLowerCase(),
                notificationsGroup.getObjectName(),
                NotificationDateTimeFormatter.format(notificationsGroup.getCreationDate()));
        }

        public static String noObjectNameMessage(NotificationsGroupedResponseDto notificationsGroup) {
            return String.format(notificationsGroup.getNotificationType().getMessageTemplate(),
                formatInitiators(notificationsGroup),
                NotificationDateTimeFormatter.format(notificationsGroup.getCreationDate()));
        }

        public static String actionMessage(NotificationsGroupedResponseDto notificationsGroup) {
            return String.format(notificationsGroup.getNotificationType().getMessageTemplate(),
                notificationsGroup.getAction());
        }

        private static String formatInitiators(NotificationsGroupedResponseDto notificationsGroup) {
            List<String> initiatorNames = notificationsGroup.getInitiatorNames().stream().toList();
            String names;
            int initiatorsCount = Math.min(initiatorNames.size(),
                notificationsGroup.getNotificationType().getShownInitiatorsCount());
            switch (initiatorNames.size()) {
                case 1 -> names = initiatorNames.getFirst();
                case 2 -> names = String.join(" and ", initiatorNames.subList(0, initiatorsCount));
                default -> names = String.join(", ", initiatorNames.subList(0, initiatorsCount))
                    + " and other users";
            }
            return names;
        }
    }
}
