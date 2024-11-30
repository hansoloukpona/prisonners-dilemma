package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class NaiveProber implements Strategy {

    private SecureRandom random = new SecureRandom();

    private Decision lastOpponentMove;

    public NaiveProber(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    // Strat�gie 5: Sondeur na�f
    @Override
    public Decision nextMove() {
        return random.nextDouble() < 0.1 ? Decision.BETRAY : lastOpponentMove;
    }
}
