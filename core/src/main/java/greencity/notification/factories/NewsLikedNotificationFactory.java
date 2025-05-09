package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EcoNewsController;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationStatus;
import greencity.notification.CommentDateTimeFormatter;
import greencity.notification.NotificationEventFactory;
import greencity.service.EcoNewsService;
import greencity.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@AllArgsConstructor
@NotificationHandler
public class NewsLikedNotificationFactory implements NotificationEventFactory {
    private final EcoNewsService ecoNewsService;
    private final NotificationService notificationService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, UserVO.class};

        return method.getDeclaringClass().equals(EcoNewsController.class)
                && method.getName().equals("like")
                && Arrays.equals(expectedParameterTypes, method.getParameterTypes());
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long newsId = (Long) args[0];
        UserVO initiator = (UserVO) args[1];

        EcoNewsVO news = ecoNewsService.findById(newsId);
        UserVO receiver = news.getAuthor();
        String objectLink = "/econews/" + newsId;

        boolean isLiked = news.getUsersLikedNews().stream()
                .anyMatch(u -> u.getId().equals(initiator.getId()));

        if (!isLiked) {
            notificationService.deleteLikeNewsNotificationIfExists(
                    initiator.getId(),
                    receiver.getId(),
                    objectLink
            );
            return null;
        }

        ZonedDateTime creationDate = ZonedDateTime.now();
        String newsTitle = news.getTitle();
        String shortenedTitle = newsTitle.length() > 20
                ? newsTitle.substring(0, 17) + "..."
                : newsTitle;

        String action = "%s likes your news %s. %s"
                .formatted(initiator.getName(), shortenedTitle, CommentDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectName("EcoNews")
                .objectLink(objectLink)
                .creationDate(creationDate)
                .status(NotificationStatus.UNREAD)
                .receiverId(receiver.getId())
                .initiatorId(initiator.getId())
                .build();
    }
}
