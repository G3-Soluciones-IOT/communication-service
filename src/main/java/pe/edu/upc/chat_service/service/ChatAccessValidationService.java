package pe.edu.upc.chat_service.service;

import feign.FeignException;
import org.springframework.stereotype.Service;
import pe.edu.upc.chat_service.integration.iam.IamIntegrationClient;
import pe.edu.upc.chat_service.integration.iam.UserResource;
import pe.edu.upc.chat_service.integration.nutritionist.ChatContactResource;
import pe.edu.upc.chat_service.integration.nutritionist.NutritionistIntegrationClient;

import java.util.List;

/**
 * Service to validate chat access based on nutritionist-patient relationships.
 */
@Service
public class ChatAccessValidationService {

    private final NutritionistIntegrationClient nutritionistClient;
    private final IamIntegrationClient iamClient;

    public ChatAccessValidationService(
            NutritionistIntegrationClient nutritionistClient,
            IamIntegrationClient iamClient) {
        this.nutritionistClient = nutritionistClient;
        this.iamClient = iamClient;
    }

    /**
     * Validates if two users can chat with each other.
     * Users can chat if:
     * 1. They have an accepted nutritionist-patient relationship
     * 2. Both users exist in IAM
     *
     * @param userId1 first user ID
     * @param userId2 second user ID
     * @return true if users can chat, false otherwise
     */
    public boolean canUsersChat(Long userId1, Long userId2) {
        try {
            UserResource user1 = iamClient.getUserById(userId1);
            UserResource user2 = iamClient.getUserById(userId2);

            if (user1 == null || user2 == null) {
                return false;
            }

            return getChatEnabledContacts(userId1).stream()
                    .anyMatch(contact -> userId2.equals(contact.contactUserId()));

        } catch (FeignException.NotFound exception) {
            return false;
        }
    }

    /**
     * Gets all chat-enabled contacts for a user (their nutritionists or patients).
     *
     * @param userId the user ID
     * @return list of normalized contacts that can chat with this user
     */
    public List<ChatContactResource> getChatEnabledContacts(Long userId) {
        try {
            return nutritionistClient.getChatContacts(userId).stream()
                    .filter(contact -> Boolean.TRUE.equals(contact.accepted()))
                    .toList();
        } catch (FeignException.NotFound exception) {
            return List.of();
        }
    }

    /**
     * Validates if a user exists in IAM.
     */
    public boolean userExists(Long userId) {
        try {
            UserResource user = iamClient.getUserById(userId);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }
}

