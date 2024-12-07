package fr.uga.l3miage.pc.prisonersdilemma.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uga.l3miage.pc.prisonersdilemma.configs.WebSocketSubscribeEventListener;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Decision;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Player implements PlayingObject {
    private static final Logger log = LoggerFactory.getLogger(Player.class);
    private String name;
    private int score = 0;
    private boolean isConnected;
    private Strategy strategy;
    private UUID playerId;
    private Decision actualRoundDecision;
    private String playerSessionId;

    @JsonIgnore
    private ArrayList<Decision> playerDecisionsHistoric = new ArrayList<>(); //Faut-il retirer l'instanciation ?

    @JsonIgnore
    private ArrayList<Integer> scoresHistoric = new ArrayList<>();

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
    public Decision play() {
        if (this.strategy == null) {
            this.actualRoundDecision = Decision.COOPERATE;
        } else {
            this.actualRoundDecision = strategy.nextMove();
        }
        return this.actualRoundDecision;
    }

    public void sendToPlayer(SimpMessagingTemplate simpMessagingTemplate, Object message) {
        if (this.isConnected) {
            WebSocketSubscribeEventListener.sendMessageToUser(simpMessagingTemplate, playerSessionId, message);
        }
    }

    public void giveUp(Strategy strategyAfterPlayerDeparture) {
        this.strategy = strategyAfterPlayerDeparture;
        this.isConnected = false;
    }

    public void updateScore(int points) {
        this.score+= points ;
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

    public boolean isConnected() {
        return isConnected;
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

    public Decision getActualRoundDecision() {
        return actualRoundDecision;
    }

    public void setActualRoundDecision(Decision actualRoundDecision) {
        this.actualRoundDecision = actualRoundDecision;
    }

    public String getPlayerSessionId() {
        return playerSessionId;
    }

    public void setPlayerSessionId(String playerSessionId) {
        this.playerSessionId = playerSessionId;
    }
}
