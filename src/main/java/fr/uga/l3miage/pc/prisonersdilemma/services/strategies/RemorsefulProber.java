package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class RemorsefulProber implements Strategy {

    private SecureRandom random = new SecureRandom();

    private Decision lastOpponentMove;

    private Decision myLastMove;

    public RemorsefulProber(Decision lastOpponentMove, Decision myLastMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.myLastMove = myLastMove;
    }

    // Stratégie 6: Sondeur repentant
    @Override
    public Decision nextMove() {
        if (random.nextDouble() < 0.1) {
            return Decision.BETRAY;
        }

        if (lastOpponentMove == Decision.BETRAY && myLastMove == Decision.BETRAY) {
            return Decision.COOPERATE;
        }
        return lastOpponentMove;
    }
}
