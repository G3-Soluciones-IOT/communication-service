package pe.edu.upc.chat_service.chat;

public record SendChatMessageResource(
        Long recipientUserId,
        String content,
        String clientMessageId
) {
}
