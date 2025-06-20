package az.aladdin.blogplatform.exception;

public record ErrorResponse(
            int status,
        String message,
        String details,
        String errorTime) {

}