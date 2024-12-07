package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;
import java.util.ArrayList;


public class PavlovRandom extends Pavlov {

    private final SecureRandom random = new SecureRandom();

    public PavlovRandom(int lastScore, Decision myLastMove) {
        super(lastScore, myLastMove);
    }

    public PavlovRandom(ArrayList<Integer> scoresHistoric, ArrayList<Decision> myMoveHistoric) {
        super(scoresHistoric, myMoveHistoric);
    }

    // Stratégie 14: Pavlov / Aléatoire
    @Override
    public Decision nextMove() {
        if (random.nextDouble() < 0.1) {
            return randomMove();
        }
        return super.nextMove();
    }
}
