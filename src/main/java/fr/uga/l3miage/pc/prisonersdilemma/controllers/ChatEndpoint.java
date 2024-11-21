package fr.uga.l3miage.pc.prisonersdilemma.controllers;

import fr.uga.l3miage.pc.prisonersdilemma.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ChatEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ChatEndpoint.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @SubscribeMapping("/posts/get")
    public Message findAll() throws IOException {

        log.info("Connection attempt by user: ");
        log.info("Session ID: ");

        Message message = new Message();

        message.setContent("Retourned!");

        log.info("Sending message: " + message);

        return message;
    }

    @SubscribeMapping("/posts/{id}/get")
    public void findOne(@DestinationVariable Long id)
            throws IOException {
        log.info("Un nouveau message est arrivé " + id);

    }

    @MessageMapping("/exchange")
    public void exchange(@Payload Message message) {
        String fromUser = message.getFrom(); // Expéditeur (nom de l'utilisateur connecté)
        String toUser = message.getTo();      // Destinataire spécifié dans le message

        log.info("Routing message from {} to {}: {}", fromUser, toUser, message.getContent());

        // Enrichir le message avant l'envoi (par exemple, ajouter un timestamp)
        message.setContent("Processed: " + message.getContent());

        // Envoyer à la file d'attente privée du destinataire
        simpMessagingTemplate.convertAndSendToUser("hans", "/dilemma-game/clients/private/direct", message);
    }

    @MessageMapping("/posts/created")
    @SendToUser("/dilemma-game/clients/private/play")
    public Message saveOne(@Payload Message message) {
        // Do error handling here
        log.info("Sending message: " + message);
        message.setContent("Salade");

        log.info("Message privé envoyé de {} à {} : {}", message.getFrom(), "Name", message.getContent());

        return message;
    }

    @MessageMapping("/posts/updated")
    @SendTo("/dilemma-game/clients/public/posts/updated")
    public Message saveTwo(Message message) {
        // Do error handling here
        log.info("Sending message: " + message);
        message.setContent("Chou Fleur");

        //simpMessagingTemplate.convertAndSend("/posts/updated", message);

        return message;
    }

    /*@MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(PostNotFoundException ex) {
        logger.debug("Post not found", ex);
        return "The requested post was not found";
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {

        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
}
