package pe.edu.upc.chat_service.integration.iam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for IAM Service integration.
 */
@FeignClient(name = "iam-service", path = "/api/v1/users")
public interface IamIntegrationClient {

    /**
     * Get user information by ID.
     *
     * @param userId the user ID
     * @return user information
     */
    @GetMapping("/{userId}")
    UserResource getUserById(@PathVariable("userId") Long userId);
}

