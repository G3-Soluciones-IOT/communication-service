package pe.edu.upc.chat_service.integration.profiles;

public record ProfileResource(
        Long id,
        String name,
        String email,
        String password,
        Boolean isActive,
        String birthDate,
        Long userProfileId
) {
}
