package pe.edu.upc.chat_service.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chat_messages")
@CompoundIndexes({
        @CompoundIndex(name = "idx_chat_messages_conversation_sent_at", def = "{'conversationId': 1, 'sentAt': 1}"),
        @CompoundIndex(name = "idx_chat_messages_recipient_read_at", def = "{'recipientUserId': 1, 'readAt': 1}")
})
public class ChatMessage {
    @Id
    private String id;
    @Indexed
    private String conversationId;
    private Long senderUserId;
    private Long recipientUserId;
    private String content;
    private Instant sentAt;
    private Instant readAt;
    private String clientMessageId;
}
