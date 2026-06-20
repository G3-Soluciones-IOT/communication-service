package pe.edu.upc.chat_service.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User entity for chat service.
 * Synced with IAM service for user authentication.
 */
@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    private String id;

    /**
     * User ID from IAM service.
     * This is the primary identifier for cross-service communication.
     */
    private Long userId;

    /**
     * Username from IAM (email).
     */
    private String nickName;

    /**
     * Full name for display purposes.
     */
    private String fullName;

    /**
     * Current connection status.
     */
    private Status status;
}