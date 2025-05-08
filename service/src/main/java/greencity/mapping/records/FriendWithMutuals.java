package greencity.mapping.records;

import greencity.entity.User;

public record FriendWithMutuals(User user, int mutualFriends) {
}
