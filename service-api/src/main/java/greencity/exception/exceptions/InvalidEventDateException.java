package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

public class InvalidEventDateException extends ConstraintDeclarationException {
    public InvalidEventDateException(String message) {
        super(message);
    }
}
