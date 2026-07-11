package pe.edu.upc.chat_service.shared;

public class ChatResourceNotFoundException extends RuntimeException {
    public ChatResourceNotFoundException(String message) {
        super(message);
    }
}
