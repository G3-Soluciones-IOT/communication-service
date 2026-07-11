package pe.edu.upc.chat_service.integration.nutritionist;

public record NutritionistResource(
        Integer id,
        Long userId,
        String fullName,
        String licenseNumber,
        String specialty,
        Integer yearsExperience,
        Boolean acceptingNewPatients,
        String bio,
        String profilePictureUrl
) {
}
