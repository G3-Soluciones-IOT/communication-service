package pe.edu.upc.chat_service.integration.nutritionist;

public record ChatContactResource(
        Long contactUserId,
        Long relationshipId,
        Integer nutritionistId,
        Long nutritionistUserId,
        Long patientUserId,
        Boolean accepted
) {
}
