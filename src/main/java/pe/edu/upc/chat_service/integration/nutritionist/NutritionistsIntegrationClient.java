package pe.edu.upc.chat_service.integration.nutritionist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "nutritionist-service", contextId = "nutritionistsIntegrationClient", path = "/api/v1/nutritionists")
public interface NutritionistsIntegrationClient {

    @GetMapping("/by-user")
    NutritionistResource getNutritionistByUserId(@RequestParam("userId") Long userId);
}
