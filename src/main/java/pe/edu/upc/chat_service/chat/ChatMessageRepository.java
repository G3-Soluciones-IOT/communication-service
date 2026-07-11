package pe.edu.upc.chat_service.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationIdOrderBySentAtDesc(String conversationId, Pageable pageable);
    List<ChatMessage> findByConversationIdAndSentAtBeforeOrderBySentAtDesc(String conversationId, java.time.Instant before, Pageable pageable);
}
