package pe.edu.upc.chat_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign interceptor to add internal service authentication header.
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String INTERNAL_HEADER = "X-Internal-Request";
    private static final String INTERNAL_SECRET = "internal-service-secret-key";

    @Override
    public void apply(RequestTemplate template) {
        template.header(INTERNAL_HEADER, INTERNAL_SECRET);
    }
}

