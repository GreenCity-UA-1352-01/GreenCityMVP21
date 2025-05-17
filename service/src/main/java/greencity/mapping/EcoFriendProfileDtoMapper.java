package greencity.mapping;

import greencity.dto.friend.EcoFriendProfileDto;
import greencity.entity.User;
import greencity.mapping.records.FriendProfileData;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class EcoFriendProfileDtoMapper extends AbstractConverter<FriendProfileData, EcoFriendProfileDto> {
    @Override
    public EcoFriendProfileDto convert(FriendProfileData source) {
        User user = source.user();
        return EcoFriendProfileDto.builder()
                .id(user.getId())
                .name(user.getFirstName())
                .profilePicturePath(user.getProfilePicturePath())
                .rating(user.getRating())
                .userCredo(user.getUserCredo())
                .isOnline(user.getLastActivityTime() != null
                    && user.getLastActivityTime().isAfter(LocalDateTime.now().minusMinutes(5)))
                .habitsInProgress(source.habitsInProgress())
                .habitsAcquired(source.habitsAcquired())
                .newsPublished(source.newsPublished())
                .build();
    }
}
