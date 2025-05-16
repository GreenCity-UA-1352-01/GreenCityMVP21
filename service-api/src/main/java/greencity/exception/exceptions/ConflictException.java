package greencity.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    /**
     * Exception thrown when a user attempts to perform an action that has already been completed,
     * such as liking an already liked event.
     * Results in HTTP 409 Conflict status.
     * @author Rostyslav Kushpit
     */
    public ConflictException(String message) {
        super(message);
    }
}
