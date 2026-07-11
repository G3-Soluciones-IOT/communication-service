package pe.edu.upc.chat_service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    public void processMessage(@Payload SendChatMessageResource message, Principal principal) {
        var senderUserId = Long.valueOf(principal.getName());
        var savedMessage = chatMessageService.save(senderUserId, message);
        var notification = ChatNotification.from(savedMessage);

        messagingTemplate.convertAndSendToUser(
                savedMessage.getRecipientUserId().toString(),
                "/queue/messages",
                notification
        );
        messagingTemplate.convertAndSendToUser(
                savedMessage.getSenderUserId().toString(),
                "/queue/messages",
                notification
        );
    }
}
