package pe.edu.upc.chat_service.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import pe.edu.upc.chat_service.config.WebSocketHandshakeInterceptor;
import pe.edu.upc.chat_service.integration.iam.IamIntegrationClient;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final IamIntegrationClient iamClient;

    public WebSocketAuthChannelInterceptor(JwtDecoder jwtDecoder, IamIntegrationClient iamClient) {
        this.jwtDecoder = jwtDecoder;
        this.iamClient = iamClient;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateConnect(accessor);
        }

        if ((StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand()))
                && accessor.getUser() == null) {
            throw new MessagingException("Authenticated WebSocket user is required");
        }

        return message;
    }

    private void authenticateConnect(StompHeaderAccessor accessor) {
        var token = tokenFrom(accessor);
        if (token == null || token.isBlank()) {
            throw new MessagingException("Authorization token is required");
        }

        jwtDecoder.decode(token);
        var user = iamClient.getCurrentUser("Bearer " + token);
        Principal principal = new UsernamePasswordAuthenticationToken(
                user.id().toString(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        accessor.setUser(principal);
    }

    private String tokenFrom(StompHeaderAccessor accessor) {
        var authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }

        var nativeAccessToken = accessor.getFirstNativeHeader(WebSocketHandshakeInterceptor.ACCESS_TOKEN_ATTRIBUTE);
        if (nativeAccessToken != null && !nativeAccessToken.isBlank()) {
            return nativeAccessToken;
        }

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            return null;
        }
        var accessToken = sessionAttributes.get(WebSocketHandshakeInterceptor.ACCESS_TOKEN_ATTRIBUTE);
        return accessToken instanceof String token ? token : null;
    }
}
