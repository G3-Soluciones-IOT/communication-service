package pe.edu.upc.chat_service.chat;

import java.time.Instant;

public record ChatMessageResource(
        String id,
        String conversationId,
        Long senderUserId,
        Long recipientUserId,
        String content,
        Instant sentAt,
        Instant readAt,
        String clientMessageId
) {
    public static ChatMessageResource from(ChatMessage message) {
        return new ChatMessageResource(
                message.getId(),
                message.getConversationId(),
                message.getSenderUserId(),
                message.getRecipientUserId(),
                message.getContent(),
                message.getSentAt(),
                message.getReadAt(),
                message.getClientMessageId()
        );
    }
}
