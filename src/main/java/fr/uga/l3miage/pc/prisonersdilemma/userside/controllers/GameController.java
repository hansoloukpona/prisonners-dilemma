package fr.uga.l3miage.pc.prisonersdilemma.userside.controllers;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.GameMapper;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameExchangeDTO;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.Game;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.ApiResponse;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.GlobalGameMap;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.Game.findTheRightGame;
import static fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.Type.*;

//User-Side

@Controller
public class GameController {

    //Au final de la business logic dans du server-side ou du user-side c'est normal

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/createGame")
    @SendToUser("/dilemma-game/clients/private/direct")
    public ApiResponse<GameDTO> createGame(@Payload GameExchangeDTO gameExchangeDTO) {
        try {
            log.info("connection successfully established " + gameExchangeDTO.getPlayerName());

            Player thePlayer1 = new Player(gameExchangeDTO.getPlayerName());
            thePlayer1.setPlayerSessionId(gameExchangeDTO.getPlayerSessionId());

            Game game = new Game(gameExchangeDTO.getRounds(), thePlayer1);
            game.setSimpMessagingTemplate(this.simpMessagingTemplate);
            GlobalGameMap gameMap = GlobalGameMap.getInstance();
            gameMap.putElement(game.getGameId(), game);
            return new ApiResponse<>(200, "OK", createGame , GameMapper.toGameDTO(game));
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
    public ApiResponse<GameDTO> joinGame(@Payload GameExchangeDTO gameExchangeDTO) {
        try {
            log.info("Arrival to join " + gameExchangeDTO);
            Player thePlayer2 = new Player(gameExchangeDTO.getPlayerName());
            thePlayer2.setPlayerSessionId(gameExchangeDTO.getPlayerSessionId());
            ApiResponse<GameDTO> apiResponse = findTheRightGame(gameExchangeDTO.getGameId()).joinGame(thePlayer2);
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
    public ApiResponse<GameDTO> playGame(@Payload GameExchangeDTO gameExchangeDTO) {
        try {
            return findTheRightGame(gameExchangeDTO.getGameId()).playGame(gameExchangeDTO.getPlayerId(), gameExchangeDTO.getPlayerDecision());
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
    
    @MessageMapping("/giveUp")
    @SendToUser("/dilemma-game/clients/private/direct")
    public ApiResponse<GameDTO> giveUpGame(@Payload GameExchangeDTO gameExchangeDTO) {
        try {
            return findTheRightGame(gameExchangeDTO.getGameId()).giveUpGame(gameExchangeDTO.getPlayerId(), gameExchangeDTO.getPlayerDecision());
        } catch (Exception e) {
            // En cas d'erreur, retourner un statut 500 (Internal Server Error)
            //TODO
            log.error("Give Up fail :" + e.getMessage());
            return new ApiResponse<>(500, "Internal Server Error", giveUpGame);
        }
        //Retire le game de la liste à la fin
    }

    //Business Logic
    public void sendToClient(String destination, String playerSessionId, Object message) {

        simpMessagingTemplate.convertAndSend(destination + "-user" + playerSessionId, message);

    }

}
