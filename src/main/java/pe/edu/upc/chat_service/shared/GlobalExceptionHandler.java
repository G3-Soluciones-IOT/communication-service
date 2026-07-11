package pe.edu.upc.chat_service.shared;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, JwtException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponseResource> handleUnauthorized(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseResource> handleForbidden(AccessDeniedException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    @ExceptionHandler(ChatResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseResource> handleNotFound(ChatResourceNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseResource> handleBadRequest(IllegalArgumentException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponseResource> handleFeignNotFound(FeignException.NotFound exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "Required resource was not found", request);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseResource> handleFeign(FeignException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_GATEWAY, "A required service is unavailable", request);
    }

    private ResponseEntity<ErrorResponseResource> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponseResource(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        ));
    }
}
