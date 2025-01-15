package fr.uga.l3miage.pc.prisonersdilemma.configs;

import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.SimpleInformationExchange;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

//User-Side (optionnel)

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

        //String username = headerAccessor.getFirstNativeHeader("username");

        // Associez ce sessionId à un utilisateur si nécessaire
        /*String destination = headerAccessor.getDestination();
        if (destination != null && destination.contains("/private/direct")) {
            //Tout va bien
        }*/

        SimpleInformationExchange message = new SimpleInformationExchange();
        message.setContent(sessionId);
        messagingTemplate.convertAndSend("/dilemma-game/clients/private/direct-user" + sessionId , message);
    }


}
