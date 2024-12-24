package fr.uga.l3miage.pc.prisonersdilemma.controllers;

import fr.uga.l3miage.pc.prisonersdilemma.entities.GameCreationDTO;
import fr.uga.l3miage.pc.prisonersdilemma.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.entities.SimpleInformationExchange;
import fr.uga.l3miage.pc.prisonersdilemma.usecases.Game;
import fr.uga.l3miage.pc.prisonersdilemma.utils.ApiResponse;
import fr.uga.l3miage.pc.prisonersdilemma.utils.GlobalGameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fr.uga.l3miage.pc.prisonersdilemma.utils.Type.*;

@Controller
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/createGame")
    @SendToUser("/dilemma-game/clients/private/direct")
    public ApiResponse<Game> createGame(@Payload GameCreationDTO gameCreationDTO) {
        try {
            log.info("connection successfully established " + gameCreationDTO.getPlayerName());

            Player thePlayer1 = new Player(gameCreationDTO.getPlayerName());
            thePlayer1.setPlayerSessionId(gameCreationDTO.getPlayerSessionId());

            Game game = new Game(gameCreationDTO.getRounds(), thePlayer1);
            game.setSimpMessagingTemplate(this.simpMessagingTemplate);
            GlobalGameMap gameMap = GlobalGameMap.getInstance();
            gameMap.putElement(game.getGameId(), game);
            return new ApiResponse<>(200, "OK", createGame ,game);
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO
            log.error("Creation error :" + e.getMessage());
            return new ApiResponse<>(500, "Internal Server Error", createGame);
        }
        //Player 1 doit récupérer son UUID
    }

    @MessageMapping("/joinGame")
    @SendToUser("/dilemma-game/clients/private/direct")
    public ApiResponse<Game> joinGame(@Payload GameCreationDTO gameCreationDTO) {
        try {
            log.info("Arrival to join " + gameCreationDTO);
            Player thePlayer2 = new Player(gameCreationDTO.getPlayerName());
            thePlayer2.setPlayerSessionId(gameCreationDTO.getPlayerSessionId());
            ApiResponse<Game> apiResponse = findTheRightGame(gameCreationDTO.getGameId()).joinGame(thePlayer2);
            sendToClient("/dilemma-game/clients/private/direct", apiResponse.getData().getThePlayer1().getPlayerSessionId(), apiResponse); //avertir le joueur 1
            //log.info("connection successfully established " + apiResponse);
            return apiResponse; //avertit le joueur 2
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO
            log.error("Join error :" + e.getMessage());
            return new ApiResponse<>(500, "Internal Server Error", joinGame);
        }
        //Player 2 doit récupérer son UUID
    }

    // Endpoint pour envoyer la décision des joueurs
    @MessageMapping("/playGame")
    @SendToUser("/dilemma-game/clients/private/direct")
    public ApiResponse<Game> playGame(@Payload GameCreationDTO gameCreationDTO) {
        try {
            return findTheRightGame(gameCreationDTO.getGameId()).playGame(gameCreationDTO.getPlayerId(), gameCreationDTO.getPlayerDecision());
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO :""
            log.error("Play error :" + e.getMessage());
            return new ApiResponse<>(500, "Internal Server Error", playGame);
        }
    }

    @MessageMapping("/getGamesList")//This should become à suscribe mapping after
    @SendTo("/dilemma-game/clients/public/getGamesList")
    public ApiResponse<List<UUID>> getAllGamesIdList() throws IOException {

        log.info("Suscription to get list of new games");

        try {
            GlobalGameMap gameMap = GlobalGameMap.getInstance();

            log.info("All Game list size is : " + gameMap.getMap().size());
            List<UUID> list = new ArrayList<>();
            list.addAll(gameMap.getGamesNotAvailableToJoin());
            log.info("Available Game list size is : " + list.size());
            return new ApiResponse<>(200, "OK", getGamesList, gameMap.getGamesNotAvailableToJoin());
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO :""
            log.error("Available game List error :" + e.getMessage());
            return new ApiResponse<>(500, "Internal Server Error", getGamesList);
        }

    }
    // Endpoint pour envoyer la décision des joueurs
    /*
    @SendTo("/notify")
    public static ApiResponse<Game> getGameState( UUID gameId,  UUID playerId, WebSocketSession playerSession ) {
        try {
            ApiResponse<Game> response = findTheRightGame(gameId).getGameState(playerId);
            GameController.sendToClient(playerSession, response);
            return response;
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            ApiResponse<Game> response = new ApiResponse<>(500, "Internal Server Error", getGameState);
            GameController.sendToClient(playerSession, response);
            return response;
        }

    }

    // Endpoint pour obtenir les résultats de la partie
    
    public static ApiResponse<String> getResults( UUID gameId,  UUID playerId) {
        try {
            //TODO
            return findTheRightGame(gameId).displayResults();
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            ApiResponse<String> response = new ApiResponse<>(500, "Internal Server Error", getResults);
            return response;
        }
    }*/

    // Endpoint pour envoyer la décision des joueurs
    
    @MessageMapping("/giveUp")
    @SendToUser("/dilemma-game/clients/private/direct")
    public ApiResponse<Game> giveUpGame(@Payload GameCreationDTO gameCreationDTO) {
        try {
            return findTheRightGame(gameCreationDTO.getGameId()).giveUpGame(gameCreationDTO.getPlayerId(), gameCreationDTO.getPlayerDecision());
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO
            log.error("Give Up fail :" + e.getMessage());
            return new ApiResponse<>(500, "Internal Server Error", giveUpGame);
        }
        //Retire le game de la liste à la fin
    }

    public void sendToClient(String destination, String playerSessionId, Object message) {

        simpMessagingTemplate.convertAndSend(destination + "-user" + playerSessionId, message);

    }

    private static Game findTheRightGame(UUID gameId) {
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        return gameMap.getElement(gameId);
    }
}
