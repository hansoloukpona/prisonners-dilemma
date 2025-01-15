package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//Business Logic

@AllArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    @Override
    public boolean playerIsPresentInTheGame(Player thePlayer) {
        if (thePlayer == null || !thePlayer.getConnected()) {
            logger.info("We need a second player, of course! Come on, try again!");
            //TODO sendMessage
            return false;
        }
        return true;
    }

    @Override
    public boolean decisionIsValid(String decision) {
        return decision.equals("COOPERATE") || decision.equals("BETRAY");
    }

    /*public void userExistAndActiveInGame (UUID playerId, Player thePlayer1, Player thePlayer2) throws Exception {
        if (!this.verifyPlayer(playerId, thePlayer1) && !this.verifyPlayer(playerId, thePlayer2))
            throw new Exception("The specified player hasn't been found");
    }*/

    /*public boolean verifyPlayer(UUID playerId, Player player) {
        /*logger.info("playerId fourni : " + playerId);
        logger.info("playerId existant dans le système : " + player.getPlayerId());
        logger.info("player est t'il connecté ? : " + player.getConnected());*/
        //C'est important de convertir en string sinon des incompatibilitées peuvent naître
        //TODO Tester le equal avec UUID directement
        /*return playerId.toString().equals(player.getPlayerId().toString()) && player.getConnected();
    }*/

}
