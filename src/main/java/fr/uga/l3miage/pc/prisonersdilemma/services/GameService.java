package fr.uga.l3miage.pc.prisonersdilemma.services;

import fr.uga.l3miage.pc.prisonersdilemma.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    public boolean playerIsPresentInTheGame(Player thePlayer) {
        if (thePlayer == null || !thePlayer.getConnected()) {
            logger.info("We need a second player, of course! Come on, try again!");
            //TODO sendMessage
            return false;
        }
        return true;
    }

    public static void decisionIsValid(String decision) throws Exception {
        if (!decision.equals("COOPERATE") && !decision.equals("BETRAY")) {
            throw new Exception("Invalid decision");
        }
    }

    public void userExistAndActiveInGame (UUID playerId, Player thePlayer1, Player thePlayer2) throws Exception {
        if (!this.verifyPlayer(playerId, thePlayer1) && !this.verifyPlayer(playerId, thePlayer2))
            throw new Exception("The specified player hasn't been found");
    }

    public boolean verifyPlayer(UUID playerId, Player player) {
        return playerId == player.getPlayerId() && player.getConnected();
    }

}
