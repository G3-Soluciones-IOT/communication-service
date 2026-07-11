package pe.edu.upc.chat_service.service;

import feign.FeignException;
import org.springframework.stereotype.Service;
import pe.edu.upc.chat_service.chat.ChatContactResponse;
import pe.edu.upc.chat_service.chat.ChatContactRole;
import pe.edu.upc.chat_service.integration.iam.IamIntegrationClient;
import pe.edu.upc.chat_service.integration.iam.UserResource;
import pe.edu.upc.chat_service.integration.nutritionist.ChatContactResource;
import pe.edu.upc.chat_service.integration.nutritionist.NutritionistsIntegrationClient;
import pe.edu.upc.chat_service.integration.profiles.ProfilesIntegrationClient;
import pe.edu.upc.chat_service.shared.ChatResourceNotFoundException;

import java.util.List;

@Service
public class ChatContactEnrichmentService {

    private final IamIntegrationClient iamClient;
    private final NutritionistsIntegrationClient nutritionistsClient;
    private final ProfilesIntegrationClient profilesClient;

    public ChatContactEnrichmentService(
            IamIntegrationClient iamClient,
            NutritionistsIntegrationClient nutritionistsClient,
            ProfilesIntegrationClient profilesClient) {
        this.iamClient = iamClient;
        this.nutritionistsClient = nutritionistsClient;
        this.profilesClient = profilesClient;
    }

    public List<ChatContactResponse> enrich(List<ChatContactResource> contacts) {
        return contacts.stream()
                .map(this::enrich)
                .toList();
    }

    private ChatContactResponse enrich(ChatContactResource contact) {
        var user = findUser(contact.contactUserId());
        if (user == null) {
            throw new ChatResourceNotFoundException("Chat contact user was not found in IAM");
        }
        var username = user.username();
        var role = contact.contactUserId().equals(contact.nutritionistUserId())
                ? ChatContactRole.NUTRITIONIST
                : ChatContactRole.PATIENT;

        var displayName = username;
        String profilePictureUrl = null;

        if (role == ChatContactRole.NUTRITIONIST) {
            var nutritionist = findNutritionist(contact.contactUserId());
            if (nutritionist != null) {
                displayName = firstNonBlank(nutritionist.fullName(), displayName);
                profilePictureUrl = nutritionist.profilePictureUrl();
            }
        } else {
            var profile = findProfile(contact.contactUserId());
            if (profile != null) {
                displayName = firstNonBlank(profile.name(), displayName);
            }
        }

        return new ChatContactResponse(
                contact.contactUserId(),
                contact.relationshipId(),
                role,
                displayName,
                username,
                profilePictureUrl,
                contact.accepted()
        );
    }

    private UserResource findUser(Long userId) {
        try {
            return iamClient.getUserById(userId);
        } catch (FeignException.NotFound exception) {
            return null;
        }
    }

    private pe.edu.upc.chat_service.integration.nutritionist.NutritionistResource findNutritionist(Long userId) {
        try {
            return nutritionistsClient.getNutritionistByUserId(userId);
        } catch (FeignException.NotFound exception) {
            return null;
        }
    }

    private pe.edu.upc.chat_service.integration.profiles.ProfileResource findProfile(Long userId) {
        try {
            return profilesClient.getProfileByUserId(userId);
        } catch (FeignException.NotFound exception) {
            return null;
        }
    }

    private String firstNonBlank(String candidate, String fallback) {
        return candidate == null || candidate.isBlank() ? fallback : candidate;
    }
}
