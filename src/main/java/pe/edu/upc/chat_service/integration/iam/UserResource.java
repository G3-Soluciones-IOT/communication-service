package pe.edu.upc.chat_service.integration.iam;

import java.util.List;

/**
 * User resource from IAM service.
 */
public record UserResource(Long id, String username, List<String> roles) {
}

