package pe.edu.upc.chat_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.chat_service.chat.ChatContactResponse;
import pe.edu.upc.chat_service.chat.ChatMessageResource;
import pe.edu.upc.chat_service.chat.ChatMessageService;
import pe.edu.upc.chat_service.security.CurrentUserService;
import pe.edu.upc.chat_service.service.ChatAccessValidationService;
import pe.edu.upc.chat_service.service.ChatContactEnrichmentService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat", description = "Patient and nutritionist communication endpoints")
public class ChatAccessController {

    private final ChatAccessValidationService chatAccessValidationService;
    private final ChatContactEnrichmentService chatContactEnrichmentService;
    private final ChatMessageService chatMessageService;
    private final CurrentUserService currentUserService;

    public ChatAccessController(
            ChatAccessValidationService chatAccessValidationService,
            ChatContactEnrichmentService chatContactEnrichmentService,
            ChatMessageService chatMessageService,
            CurrentUserService currentUserService) {
        this.chatAccessValidationService = chatAccessValidationService;
        this.chatContactEnrichmentService = chatContactEnrichmentService;
        this.chatMessageService = chatMessageService;
        this.currentUserService = currentUserService;
    }

    @Operation(summary = "Get current user's chat contacts")
    @GetMapping("/me/contacts")
    public ResponseEntity<List<ChatContactResponse>> getMyChatContacts(Authentication authentication) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var contacts = chatAccessValidationService.getChatEnabledContacts(currentUserId);
        return ResponseEntity.ok(chatContactEnrichmentService.enrich(contacts));
    }

    @Operation(summary = "Get messages for a conversation with an accepted contact")
    @GetMapping("/conversations/{contactUserId}/messages")
    public ResponseEntity<List<ChatMessageResource>> getConversationMessages(
            Authentication authentication,
            @PathVariable Long contactUserId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before) {
        var currentUserId = currentUserService.currentUserId(authentication);
        var messages = chatMessageService.findChatMessages(currentUserId, contactUserId, limit, before).stream()
                .map(ChatMessageResource::from)
                .toList();
        return ResponseEntity.ok(messages);
    }
}
