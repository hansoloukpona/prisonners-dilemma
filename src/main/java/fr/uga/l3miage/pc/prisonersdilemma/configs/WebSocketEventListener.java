package fr.uga.l3miage.pc.prisonersdilemma.configs;

import fr.uga.l3miage.pc.prisonersdilemma.entities.Message;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

//@Component //Ceci est une option, On ne se servira pas de ceci pour ce projet
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String username = headerAccessor.getFirstNativeHeader("username");

        // Associez ce sessionId � un utilisateur si n�cessaire
        /*String destination = headerAccessor.getDestination();
        if (destination != null && destination.contains("/private/direct")) {
            //Tout va bien
        }*/

        Message message = new Message();
        message.setContent(sessionId);
        messagingTemplate.convertAndSend("/dilemma-game/clients/private/direct-user" + sessionId , message);
    }


}