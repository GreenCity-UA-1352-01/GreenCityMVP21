package greencity.projection;

public interface UserWithMutualFriendsProjection {
    Long getId();

    String getName();

    String getFirstName();

    String getCity();

    String getProfilePicture();

    Double getRating();

    Integer getMutualFriendsCount();
}
