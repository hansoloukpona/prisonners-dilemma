package fr.uga.l3miage.pc.prisonersdilemma.configs;

import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.SimpleExchangeDTO;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.ApiResponse;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.Type.sessionIdInitiation;

//User-Side

@Component
public class WebSocketSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketSubscribeEventListener(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Récupération du sessionId et de la destination
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.contains("/private/direct")) {
            SimpleExchangeDTO message = new SimpleExchangeDTO();
            message.setContent(sessionId);
            sendMessageToUser(simpMessagingTemplate, sessionId, new ApiResponse<>(200, "OK", sessionIdInitiation ,message));
        }
    }

    @ResponseBody
    public static void sendMessageToUser(SimpMessagingTemplate simpMessagingTemplate, String sessionId, Object payload) {

        // Envoi du message à l'utilisateur via le canal privé
        simpMessagingTemplate.convertAndSend("/dilemma-game/clients/private/direct-user" + sessionId, payload);
    }
}
