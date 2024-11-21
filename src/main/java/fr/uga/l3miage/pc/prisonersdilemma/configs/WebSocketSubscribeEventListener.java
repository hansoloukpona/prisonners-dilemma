package fr.uga.l3miage.pc.prisonersdilemma.configs;

import fr.uga.l3miage.pc.prisonersdilemma.entities.Message;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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
        //String usernameWhat = headerAccessor.getUser().getName(); //User is link to Principal

        String username = headerAccessor.getFirstNativeHeader("username");
        System.out.println("username : " + username);

        if (destination != null && destination.contains("/private/direct")) {
            sendMessageToUser(simpMessagingTemplate, sessionId);
        }
    }

    public static void sendMessageToUser(SimpMessagingTemplate simpMessagingTemplate, String sessionId) {
        // Vous pouvez utiliser le sessionId pour cibler spécifiquement cet utilisateur
        Message message = new Message();
        message .setContent(sessionId);

        // Envoi du message à l'utilisateur via le canal privé
        simpMessagingTemplate.convertAndSend("/dilemma-game/clients/private/direct-user" + sessionId, message);
    }
}

