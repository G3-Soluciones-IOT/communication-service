package pe.edu.upc.chat_service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pe.edu.upc.chat_service.chatroom.ChatRoomService;
import pe.edu.upc.chat_service.service.ChatAccessValidationService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final ChatAccessValidationService chatAccessValidationService;

    public ChatMessage save(Long senderUserId, SendChatMessageResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Message payload is required");
        }
        validateContent(resource.content());
        validateCanChat(senderUserId, resource.recipientUserId());

        var sentAt = Instant.now();
        var conversationId = chatRoomService
                .getConversationId(senderUserId, resource.recipientUserId(), true)
                .orElseThrow();

        var chatMessage = ChatMessage.builder()
                .conversationId(conversationId)
                .senderUserId(senderUserId)
                .recipientUserId(resource.recipientUserId())
                .content(resource.content())
                .sentAt(sentAt)
                .clientMessageId(resource.clientMessageId())
                .build();

        repository.save(chatMessage);
        chatRoomService.touch(conversationId, sentAt);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(Long currentUserId, Long contactUserId, int limit, Instant before) {
        validateCanChat(currentUserId, contactUserId);
        var conversationId = chatRoomService.getConversationId(currentUserId, contactUserId, false);
        if (conversationId.isEmpty()) {
            return List.of();
        }

        var boundedLimit = Math.max(1, Math.min(limit, 100));
        var pageable = PageRequest.of(0, boundedLimit);
        var messages = before == null
                ? repository.findByConversationIdOrderBySentAtDesc(conversationId.get(), pageable)
                : repository.findByConversationIdAndSentAtBeforeOrderBySentAtDesc(conversationId.get(), before, pageable);

        Collections.reverse(messages);
        return messages;
    }

    private void validateCanChat(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null || userId1.equals(userId2)
                || !chatAccessValidationService.canUsersChat(userId1, userId2)) {
            throw new org.springframework.security.access.AccessDeniedException("Users are not allowed to chat");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }
    }
}
