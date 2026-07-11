package pe.edu.upc.chat_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pe.edu.upc.chat_service.integration.iam.IamIntegrationClient;

@Service
public class CurrentUserService {

    private final IamIntegrationClient iamClient;

    public CurrentUserService(IamIntegrationClient iamClient) {
        this.iamClient = iamClient;
    }

    public Long currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authenticated user is required");
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            var tokenValue = jwtAuthentication.getToken().getTokenValue();
            return iamClient.getCurrentUser("Bearer " + tokenValue).id();
        }

        return Long.valueOf(authentication.getName());
    }
}
