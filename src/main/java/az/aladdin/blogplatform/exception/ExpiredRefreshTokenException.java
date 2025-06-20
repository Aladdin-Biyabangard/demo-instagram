package az.aladdin.blogplatform.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException(String message) {
        super(message);
    }
}