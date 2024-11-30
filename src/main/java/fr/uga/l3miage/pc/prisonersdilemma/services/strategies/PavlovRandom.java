package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class PavlovRandom implements Strategy {

    private SecureRandom random = new SecureRandom();

    private int lastScore;

    private Decision myLastMove;

    private Pavlov pavlov;

    public PavlovRandom(int lastScore, Decision myLastMove) {
        this.lastScore = lastScore;
        this.myLastMove = myLastMove;
        this.pavlov = new Pavlov(lastScore, myLastMove);
    }

    // Stratégie 14: Pavlov / Aléatoire
    @Override
    public Decision nextMove() {
        if (random.nextDouble() < 0.1) {
            return randomMove();
        }
        pavlov.setLastScore(lastScore);
        pavlov.setMyLastMove(myLastMove);
        return pavlov.nextMove();
    }
}
