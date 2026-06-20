package pe.edu.upc.chat_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.audit.AuditEventsEndpointAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {AuditEventsEndpointAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {
	"pe.edu.upc.chat_service.integration",
	"pe.edu.upc.chat_service.config"
})
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);
	}

}
