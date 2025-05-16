package greencity.enums;

import greencity.dto.notification.BaseNotificationResponseDto;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationObjectType {
    HABIT("/habit/%s", LinkBuilder::objectLink),
    EVENT("/event/%s", LinkBuilder::objectLink),
    USER("/user/%s", LinkBuilder::objectLink),
    ECO_NEWS("/econews/%s", LinkBuilder::objectLink);

    private final String linkTemplate;
    private final Function<BaseNotificationResponseDto, String> linkBuilder;

    private static class LinkBuilder {
        public static String objectLink(BaseNotificationResponseDto notification) {
            return String.format(notification.getObjectType().getLinkTemplate(), notification.getObjectId());
        }
    }
}
