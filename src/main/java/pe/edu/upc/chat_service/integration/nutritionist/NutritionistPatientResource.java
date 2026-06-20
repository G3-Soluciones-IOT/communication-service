package pe.edu.upc.chat_service.integration.nutritionist;

import java.util.Date;

/**
 * Nutritionist-Patient relationship resource.
 */
public record NutritionistPatientResource(
        Long id,
        Integer nutritionistId,
        Long patientUserId,
        String serviceType,
        Date startDate,
        Date scheduledAt,
        Boolean accepted,
        Date requestedAt
) {
}

