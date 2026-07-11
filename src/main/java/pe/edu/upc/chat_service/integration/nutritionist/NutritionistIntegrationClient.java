package pe.edu.upc.chat_service.integration.nutritionist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client for Nutritionist Service integration.
 */
@FeignClient(name = "nutritionist-service", path = "/api/v1/nutritionist-patients")
public interface NutritionistIntegrationClient {

    /**
     * Get all nutritionists for a specific patient.
     *
     * @param patientUserId the patient user ID
     * @return list of nutritionist-patient relationships
     */
    @GetMapping("/patient/{id}")
    List<NutritionistPatientResource> getNutritionistsOfPatient(@PathVariable("id") Long patientUserId);

    /**
     * Get all patients for a specific nutritionist.
     *
     * @param nutritionistId the nutritionist ID
     * @return list of nutritionist-patient relationships
     */
    @GetMapping("/nutritionist/{id}")
    List<NutritionistPatientResource> getPatientsOfNutritionist(@PathVariable("id") Integer nutritionistId);

    @GetMapping("/chat-contacts/{userId}")
    List<ChatContactResource> getChatContacts(@PathVariable("userId") Long userId);
}

