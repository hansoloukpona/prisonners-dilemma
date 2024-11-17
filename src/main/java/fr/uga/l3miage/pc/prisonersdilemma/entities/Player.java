package fr.uga.l3miage.pc.prisonersdilemma.entities;

import fr.uga.l3miage.pc.prisonersdilemma.controllers.GameController;
import fr.uga.l3miage.pc.prisonersdilemma.services.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.utils.Decision;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public class Player implements PlayingObject {
    private String name;
    private int score = 0;
    private boolean isConnected;
    private Strategy strategy;
    private UUID playerId;
    private Decision actualRoundDecision;
    private Decision opponentLastDecision;

    private WebSocketSession playerSession;

    public Player(String name, WebSocketSession playerSession) {
        this.playerId = UUID.randomUUID();
        this.name = name;
        this.isConnected = true;
        this.playerSession = playerSession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean getConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public WebSocketSession getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(WebSocketSession playerSession) {
        this.playerSession = playerSession;
    }

    public Decision getActualRoundDecision() {
        return actualRoundDecision;
    }

    public void setActualRoundDecision(Decision actualRoundDecision) {
        this.actualRoundDecision = actualRoundDecision;
    }

    @Override
    public Decision play() /*throws Exception*/ {
        if (!this.isConnected && this.strategy != null) {
            actualRoundDecision = strategy.nextMove(opponentLastDecision);
        }
        return this.actualRoundDecision;
    }

    public void sendToPlayer(Object message) {
        if (this.isConnected) {
            GameController.sendToClient(playerSession, message);
        }
    }

    public Decision getOpponentLastDecision() {
        return opponentLastDecision;
    }

    public void setOpponentLastDecision(Decision opponentLastDecision) {
        this.opponentLastDecision = opponentLastDecision;
    }

    public void giveUp(Strategy strategyAfterPlayerDeparture) {
        this.strategy = strategyAfterPlayerDeparture;
        this.isConnected = false;
    }

    public void updateScore(int points) {
        this.score+= points ;
    }


}
