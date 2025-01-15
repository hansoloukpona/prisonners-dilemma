package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Data;

import java.util.ArrayList;

@Data
public class GrimTrigger implements Strategy {

    private final ArrayList<Decision> opponentMoveHistoric;

    public GrimTrigger(ArrayList<Decision> opponentMoveHistoric) {
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    // Stratégie 12: Rancunier - Coopère jusqu'à la première trahison, puis toujours trahir
    @Override
    public Decision nextMove() {

        if (haveMyOpponentOnceBetray()) {
            return Decision.BETRAY;
        }
        return Decision.COOPERATE;
    }

    private boolean haveMyOpponentOnceBetray() {
        return  opponentMoveHistoric.stream().anyMatch(decision -> decision == Decision.BETRAY);
    }

}
