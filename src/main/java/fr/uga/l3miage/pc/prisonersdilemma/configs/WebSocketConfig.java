package fr.uga.l3miage.pc.prisonersdilemma.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//User-Side

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Define the WebSocket endpoint with proper SockJS support
        registry.addEndpoint("/dilemma-game/server/hi")
                //.setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy))
                .setAllowedOrigins("*")
                /*.withSockJS()*/;

        registry.addEndpoint("/dilemma-game/server/hiJS")
                //.setHandshakeHandler(new DefaultHandshakeHandler(upgradeStrategy))
                .setAllowedOrigins("*")
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure the broker to handle messages sent to the "/api/game/clients" topic
        config.enableSimpleBroker("/dilemma-game/clients/public", "/dilemma-game/clients/private");
        config.setApplicationDestinationPrefixes("/dilemma-game/server");
        config.setUserDestinationPrefix("/player"); // Destinations privées pour les utilisateurs

    }

}
