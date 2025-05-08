package greencity.mapping.records;

import greencity.entity.User;

public record FriendProfileData(User user, int habitsInProgress, int habitsAcquired, long newsPublished) {
}
