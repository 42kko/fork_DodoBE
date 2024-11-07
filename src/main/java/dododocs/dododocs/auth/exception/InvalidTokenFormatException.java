package dododocs.dododocs.auth.exception;

public class InvalidTokenFormatException extends RuntimeException {
    public InvalidTokenFormatException(String message) {
        super(message);
    }
}
