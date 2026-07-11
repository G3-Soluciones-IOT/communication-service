package pe.edu.upc.chat_service.integration.profiles;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profiles-service", path = "/api/v1/profiles")
public interface ProfilesIntegrationClient {

    @GetMapping("/by-user/{userId}")
    ProfileResource getProfileByUserId(@PathVariable("userId") Long userId);
}
