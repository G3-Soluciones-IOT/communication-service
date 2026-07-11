package pe.edu.upc.chat_service.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private String id;
    private String conversationId;
    private Long senderUserId;
    private Long recipientUserId;
    private String content;
    private java.time.Instant sentAt;
    private String clientMessageId;

    public static ChatNotification from(ChatMessage message) {
        return ChatNotification.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderUserId(message.getSenderUserId())
                .recipientUserId(message.getRecipientUserId())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .clientMessageId(message.getClientMessageId())
                .build();
    }
}
