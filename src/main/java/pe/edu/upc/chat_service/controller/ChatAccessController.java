package pe.edu.upc.chat_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.chat_service.service.ChatAccessValidationService;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for chat access and validation endpoints.
 */
@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat Access", description = "Chat access validation and contact management")
public class ChatAccessController {

    private final ChatAccessValidationService chatAccessValidationService;

    public ChatAccessController(ChatAccessValidationService chatAccessValidationService) {
        this.chatAccessValidationService = chatAccessValidationService;
    }

    /**
     * Get all chat-enabled contacts for a user.
     * Returns users that have an accepted nutritionist-patient relationship.
     */
    @Operation(
            summary = "Get chat-enabled contacts",
            description = "Returns all users that the specified user can chat with (accepted nutritionist-patient relationships)"
    )
    @GetMapping("/contacts/{userId}")
    public ResponseEntity<List<Long>> getChatContacts(@PathVariable Long userId) {
        if (!chatAccessValidationService.userExists(userId)) {
            return ResponseEntity.notFound().build();
        }

        List<Long> contacts = chatAccessValidationService.getChatEnabledContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Validate if two users can chat with each other.
     */
    @Operation(
            summary = "Validate chat access",
            description = "Validates if two users have permission to chat with each other"
    )
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateChatAccess(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {

        boolean canChat = chatAccessValidationService.canUsersChat(userId1, userId2);
        return ResponseEntity.ok(Map.of("canChat", canChat));
    }

    /**
     * Check if a user exists in the system.
     */
    @Operation(
            summary = "Check user existence",
            description = "Validates if a user exists in IAM service"
    )
    @GetMapping("/users/{userId}/exists")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(@PathVariable Long userId) {
        boolean exists = chatAccessValidationService.userExists(userId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}

