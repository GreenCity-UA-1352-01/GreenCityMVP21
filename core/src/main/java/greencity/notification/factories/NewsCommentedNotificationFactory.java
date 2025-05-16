package greencity.notification.factories;

import greencity.annotations.NotificationHandler;
import greencity.controller.EcoNewsCommentController;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationOrigin;
import greencity.enums.NotificationStatus;
import greencity.notification.NotificationEventFactory;
import greencity.service.EcoNewsService;
import greencity.notification.CommentDateTimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@AllArgsConstructor
@NotificationHandler
public class NewsCommentedNotificationFactory implements NotificationEventFactory {
    private final EcoNewsService ecoNewsService;

    @Override
    public boolean supports(Method method) {
        Class<?>[] expectedParameterTypes = {Long.class, AddEcoNewsCommentDtoRequest.class, UserVO.class};

        return method.getDeclaringClass().equals(EcoNewsCommentController.class)
                && method.getName().equals("save")
                && Arrays.equals(method.getParameterTypes(), expectedParameterTypes);
    }

    @Override
    public NotificationRequestDto createEvent(Object[] args) {
        Long newsId = (Long) args[0];
        UserVO initiator = (UserVO) args[2];

        ZonedDateTime creationDate = ZonedDateTime.now();

        EcoNewsVO news = ecoNewsService.findById(newsId);
        UserVO receiver = news.getAuthor();

        String newsTitle = news.getTitle();
        String shortenedTitle = newsTitle.length() > 20
                ? newsTitle.substring(0, 17) + "..."
                : newsTitle;

        String action = "%s commented on your news %s. %s"
                .formatted(initiator.getName(), shortenedTitle, CommentDateTimeFormatter.format(creationDate));

        return NotificationRequestDto.builder()
                .action(action)
                .objectName("EcoNews")
                .objectLink("/econews/comments/" + newsId)
                .creationDate(creationDate)
                .status(NotificationStatus.UNREAD)
                .receiverId(receiver.getId())
                .initiatorId(initiator.getId())
                .origin(NotificationOrigin.GREEN_CITY)
                .build();
    }
}
