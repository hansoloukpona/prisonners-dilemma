package fr.uga.l3miage.pc.prisonersdilemma.controllers;

import fr.uga.l3miage.pc.prisonersdilemma.entities.GameCreationDTO;
import fr.uga.l3miage.pc.prisonersdilemma.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.usecases.Game;
import fr.uga.l3miage.pc.prisonersdilemma.utils.ApiResponse;
import fr.uga.l3miage.pc.prisonersdilemma.utils.GlobalGameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

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
            GlobalGameMap gameMap = GlobalGameMap.getInstance();
            gameMap.putElement(game.getGameId(), game);
            return new ApiResponse<>(200, "OK", createGame ,game);
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO
            return new ApiResponse<>(500, "Internal Server Error", createGame);
        }
        //Player 1 doit récupérer son UUID
    }

    @MessageMapping("/joinGame")
    @SendToUser("/dilemma-game/clients/private/canal")
    public ApiResponse<Game> joinGame(@Payload GameCreationDTO gameCreationDTO) {
        try {
            Player thePlayer2 = new Player(gameCreationDTO.getPlayerName());
            thePlayer2.setPlayerSessionId(gameCreationDTO.getPlayerSessionId());
            /* sendToClient("/dilemma-game/clients/private/canal", thePlayer2.getPlayerSessionId(), response); */
            return findTheRightGame(gameCreationDTO.getGameId()).joinGame(thePlayer2);
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO
            return new ApiResponse<>(500, "Internal Server Error", joinGame);
        }
        //Player 2 doit récupérer son UUID
    }

    // Endpoint pour envoyer la décision des joueurs
    @MessageMapping("/playGame")
    @SendToUser("/dilemma-game/clients/private/canal")
    public ApiResponse<Game> playGame(@Payload GameCreationDTO gameCreationDTO) {
        try {
            //sendToClient("/dilemma-game/clients/private/canal", gameCreationDTO.getPlayerSessionId(), response);
            return findTheRightGame(gameCreationDTO.getGameId()).playGame(gameCreationDTO.getPlayerId(), gameCreationDTO.getPlayerDecision());
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO :""
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
    }

    // Endpoint pour envoyer la décision des joueurs
    
    //@PostMapping("/{gameId}/giveup")
    public static ApiResponse<Game> giveUpGame( UUID gameId,  UUID playerId, String strategyToApply, WebSocketSession playerSession) {
        try {
            ApiResponse<Game> response = findTheRightGame(gameId).giveUpGame(playerId, strategyToApply);
            GameController.sendToClient(playerSession, response);
            return response;
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            ApiResponse<Game> response = new ApiResponse<>(500, "Internal Server Error", giveUpGame);
            GameController.sendToClient(playerSession, response);
            return response;
        }
    }*/

    
    //@GetMapping("/gamelist")
    public static ApiResponse<List<UUID>> getGamesList() {
        try {
            GlobalGameMap gameMap = GlobalGameMap.getInstance();

            List<UUID> availableGames = new ArrayList<>();

            for (Map.Entry<UUID, Game> entry : gameMap.getMap().entrySet()) {
                if (entry.getValue().isAvailableToJoin()) {
                    availableGames.add(entry.getKey());
                }
            }
            ApiResponse<List<UUID>> response = new ApiResponse<>(200, "OK", getGamesList, availableGames);
            return response;
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            ApiResponse<List<UUID>> response = new ApiResponse<>(500, "Internal Server Error", getGamesList);

            return response;
        }
    }
    
    //@PostMapping("/{gameId}/endgame")
    public ApiResponse<Void> endGame( UUID gameId,  UUID playerId) {
        return null;
    }

    public void sendToClient(String destination, String playerSessionId, Object message) {

        simpMessagingTemplate.convertAndSend(destination + "-user" + playerSessionId, message);

    }

    private static Game findTheRightGame(UUID gameId) {
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        return gameMap.getElement(gameId);
    }
}
