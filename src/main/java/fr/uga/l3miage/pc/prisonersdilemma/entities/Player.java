package fr.uga.l3miage.pc.prisonersdilemma.entities;

import fr.uga.l3miage.pc.prisonersdilemma.controllers.GameController;
import fr.uga.l3miage.pc.prisonersdilemma.services.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.utils.Decision;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@AllArgsConstructor
@Data
public class Player implements PlayingObject {
    private String name;
    private int score = 0;
    private boolean isConnected;
    private Strategy strategy;
    private UUID playerId;
    private Decision actualRoundDecision;
    private Decision opponentLastDecision;

    private String playerSessionId;

    public Player(String name) {
        this.playerId = UUID.randomUUID();
        this.name = name;
        this.isConnected = true;
    }

    public boolean getConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    @Override
    public Decision play() /*throws Exception*/ {
        if (!this.isConnected && this.strategy != null) {
            actualRoundDecision = strategy.nextMove(opponentLastDecision);
        }
        return this.actualRoundDecision;
    }

    public void sendToPlayer(String destination, Object message) {
        if (this.isConnected) {
            GameController.sendToClient(destination, playerSessionId, message);
        }
    }

    public void giveUp(Strategy strategyAfterPlayerDeparture) {
        this.strategy = strategyAfterPlayerDeparture;
        this.isConnected = false;
    }

    public void updateScore(int points) {
        this.score+= points ;
    }


}
