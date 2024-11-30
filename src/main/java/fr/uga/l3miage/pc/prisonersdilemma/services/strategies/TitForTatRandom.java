package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import java.security.SecureRandom;


public class TitForTatRandom implements Strategy {

	private SecureRandom random = new SecureRandom();

    private Decision lastOpponentMove;

    public TitForTatRandom(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    @Override
    public Decision nextMove() {
        return random.nextDouble() < 0.1 ? random.nextBoolean() ? Decision.COOPERATE : Decision.BETRAY : lastOpponentMove;
    }
}
