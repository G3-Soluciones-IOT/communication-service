package pe.edu.upc.chat_service.chatroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getConversationId(
            Long senderUserId,
            Long recipientUserId,
            boolean createNewRoomIfNotExists
    ) {
        var conversationId = conversationId(senderUserId, recipientUserId);

        return chatRoomRepository.findByConversationId(conversationId)
                .map(ChatRoom::getConversationId)
                .or(() -> createNewRoomIfNotExists
                        ? Optional.of(createChatRoom(conversationId, senderUserId, recipientUserId).getConversationId())
                        : Optional.empty());
    }

    public void touch(String conversationId, Instant lastMessageAt) {
        chatRoomRepository.findByConversationId(conversationId).ifPresent(chatRoom -> {
            chatRoom.setLastMessageAt(lastMessageAt);
            chatRoomRepository.save(chatRoom);
        });
    }

    private ChatRoom createChatRoom(String conversationId, Long senderUserId, Long recipientUserId) {
        var orderedParticipants = orderedParticipants(senderUserId, recipientUserId);
        var chatRoom = ChatRoom
                .builder()
                .conversationId(conversationId)
                .participantUserIds(orderedParticipants)
                .lastMessageAt(Instant.now())
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    private String conversationId(Long senderUserId, Long recipientUserId) {
        var participants = orderedParticipants(senderUserId, recipientUserId);
        return participants.get(0) + "_" + participants.get(1);
    }

    private List<Long> orderedParticipants(Long senderUserId, Long recipientUserId) {
        return senderUserId <= recipientUserId
                ? List.of(senderUserId, recipientUserId)
                : List.of(recipientUserId, senderUserId);
    }
}
