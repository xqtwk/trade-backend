package grade.tradeback.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import grade.tradeback.security.jwt.JwtService;
import grade.tradeback.websocket.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker

public class WebsocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer{

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public WebsocketConfig(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true; // Disable CSRF for WebSockets
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/ws/**").permitAll();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        CustomHandshakeHandler customHandshakeHandler = new CustomHandshakeHandler(jwtService);  // Create an instance
        registry.addEndpoint("/ws")
                .setHandshakeHandler(customHandshakeHandler)
                .setAllowedOrigins("http://localhost:4200", "https://pixelpact.eu")
                .withSockJS();

        registry.addEndpoint("/ws/trade")
                .setHandshakeHandler(customHandshakeHandler)
                .setAllowedOrigins("http://localhost:4200", "https://pixelpact.eu")
                .withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

}
