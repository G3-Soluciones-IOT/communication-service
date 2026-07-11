package pe.edu.upc.chat_service.chat;

public record ChatContactResponse(
        Long contactUserId,
        Long relationshipId,
        ChatContactRole role,
        String displayName,
        String username,
        String profilePictureUrl,
        Boolean accepted
) {
}
