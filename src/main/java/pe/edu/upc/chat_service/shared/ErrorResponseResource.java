package pe.edu.upc.chat_service.shared;

import java.time.Instant;

public record ErrorResponseResource(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
