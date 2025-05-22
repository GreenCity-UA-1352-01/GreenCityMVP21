package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationType;
import greencity.enums.NotificationOrigin;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link UserTagService}.
 */
@Service
@RequiredArgsConstructor
public class UserTagServiceImpl implements UserTagService {
    private final UserRepo userRepo;
    private final NotificationService notificationService;

    /**
     * Pattern to match @username or #username in text.
     * The username can contain letters, numbers, underscores, and hyphens.
     */
    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("@([\\w-]+)");

    @Override
    public List<UserVO> findUsersByName(String name) {
        List<User> users = userRepo.findByNameContainingIgnoreCase(name);
        return users.stream()
            .map(this::convertToUserVO)
            .collect(Collectors.toList());
    }

    /**
     * Converts User entity to UserVO DTO.
     *
     * @param user the User entity to convert
     * @return the converted UserVO DTO
     */
    private UserVO convertToUserVO(User user) {
        return UserVO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .userStatus(user.getUserStatus())
            .build();
    }

    @Override
    public void processUserMentions(String commentText, Long initiatorId, String objectType, Long objectId, Long commentId) {
        if (commentText == null || commentText.isEmpty()) {
            return;
        }

        Matcher matcher = USER_MENTION_PATTERN.matcher(commentText);
        Set<Long> mentionedUserIds = new HashSet<>();

        while (matcher.find()) {
            String username = matcher.group(1);
            List<User> users = userRepo.findByNameContainingIgnoreCase(username);

            users.stream()
                .filter(user -> user.getName().equalsIgnoreCase(username))
                .map(User::getId)
                .forEach(mentionedUserIds::add);
        }

        if (!mentionedUserIds.isEmpty()) {
            NotificationObjectType notificationObjectType = NotificationObjectType.valueOf(objectType.toUpperCase());

            NotificationRequestDto notificationRequestDto = NotificationRequestDto.builder()
                .notificationType(NotificationType.USER_MENTION)
                .initiatorId(initiatorId)
                .receiverIds(mentionedUserIds)
                .objectId(objectId)
                .objectType(notificationObjectType)
                .objectName(objectType + " comment")
                .action("You were mentioned in a comment")
                .creationDate(ZonedDateTime.now())
                .origin(NotificationOrigin.GREEN_CITY)
                .commentId(commentId)
                .build();

            notificationService.createNotifications(notificationRequestDto);
        }
    }
}
