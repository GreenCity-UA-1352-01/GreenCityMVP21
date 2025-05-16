package greencity.exception.exceptions;

public class InvalidOriginException extends RuntimeException {
    /**
     * Exception we get when we try to convert inappropriate string into {@code NotificationOrigin}.
     *
     * @see greencity.enums.NotificationOrigin
     */
    public InvalidOriginException(String message) {
        super(message);
    }
}
