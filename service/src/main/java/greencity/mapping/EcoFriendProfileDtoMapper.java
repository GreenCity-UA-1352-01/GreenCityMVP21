package greencity.mapping;

import greencity.dto.friend.EcoFriendProfileDto;
import greencity.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EcoFriendProfileDtoMapper {
    public EcoFriendProfileDto convert(User user, int habitsInProgress, int habitsAcquired, long newsPublished) {
        return EcoFriendProfileDto.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .profilePicturePath(user.getProfilePicturePath())
                .rating(user.getRating())
                .userCredo(user.getUserCredo())
                .isOnline(user.getLastActivityTime() != null &&
                        user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .habitsInProgress(habitsInProgress)
                .habitsAcquired(habitsAcquired)
                .newsPublished(newsPublished)
                .build();
    }
}
