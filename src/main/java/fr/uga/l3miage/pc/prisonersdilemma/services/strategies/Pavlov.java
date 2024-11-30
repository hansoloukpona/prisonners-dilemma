package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

@Data
public class Pavlov implements Strategy {

    private int lastScore;
    private Decision myLastMove;

    public Pavlov(int lastScore, Decision myLastMove) {
        this.lastScore = lastScore;
        this.myLastMove = myLastMove;
    }

    // Stratégie 13: Pavlov - Si 5 ou 3 points obtenus au tour précédent, répéter le dernier choix
    @Override
    public Decision nextMove() {
        if (lastScore == 3 || lastScore == 5) {
            return myLastMove;
        }
        return myLastMove == Decision.COOPERATE ? Decision.BETRAY : Decision.COOPERATE;
    }
}
