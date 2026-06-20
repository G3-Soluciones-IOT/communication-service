package pe.edu.upc.chat_service.service;

import feign.FeignException;
import org.springframework.stereotype.Service;
import pe.edu.upc.chat_service.integration.iam.IamIntegrationClient;
import pe.edu.upc.chat_service.integration.iam.UserResource;
import pe.edu.upc.chat_service.integration.nutritionist.NutritionistIntegrationClient;
import pe.edu.upc.chat_service.integration.nutritionist.NutritionistPatientResource;

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
            // Verify both users exist in IAM
            UserResource user1 = iamClient.getUserById(userId1);
            UserResource user2 = iamClient.getUserById(userId2);

            if (user1 == null || user2 == null) {
                return false;
            }

            // Check if they have an accepted relationship
            return hasAcceptedRelationship(userId1, userId2);

        } catch (FeignException.NotFound e) {
            return false;
        } catch (Exception e) {
            System.err.println("Error validating chat access: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if there's an accepted nutritionist-patient relationship between users.
     */
    private boolean hasAcceptedRelationship(Long userId1, Long userId2) {
        try {
            // Check if user1 is a patient of user2 (user2 is nutritionist)
            List<NutritionistPatientResource> relationshipsAsPatient =
                nutritionistClient.getNutritionistsOfPatient(userId1);

            boolean foundAsPatient = relationshipsAsPatient.stream()
                    .anyMatch(rel -> rel.patientUserId().equals(userId1)
                            && rel.accepted()
                            && rel.nutritionistId().longValue() == userId2);

            if (foundAsPatient) {
                return true;
            }

            // Check if user1 is a nutritionist of user2 (user2 is patient)
            List<NutritionistPatientResource> relationshipsAsNutritionist =
                nutritionistClient.getNutritionistsOfPatient(userId2);

            return relationshipsAsNutritionist.stream()
                    .anyMatch(rel -> rel.patientUserId().equals(userId2)
                            && rel.accepted()
                            && rel.nutritionistId().longValue() == userId1);

        } catch (Exception e) {
            System.err.println("Error checking relationship: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all chat-enabled contacts for a user (their nutritionists or patients).
     *
     * @param userId the user ID
     * @return list of user IDs that can chat with this user
     */
    public List<Long> getChatEnabledContacts(Long userId) {
        try {
            List<NutritionistPatientResource> relationships =
                nutritionistClient.getNutritionistsOfPatient(userId);

            return relationships.stream()
                    .filter(NutritionistPatientResource::accepted)
                    .map(rel -> {
                        // If this user is the patient, return the nutritionist ID
                        if (rel.patientUserId().equals(userId)) {
                            return rel.nutritionistId().longValue();
                        }
                        // Otherwise, return the patient ID
                        return rel.patientUserId();
                    })
                    .distinct()
                    .toList();

        } catch (Exception e) {
            System.err.println("Error getting chat contacts: " + e.getMessage());
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

