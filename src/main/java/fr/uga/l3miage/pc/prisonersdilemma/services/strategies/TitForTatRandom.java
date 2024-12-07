package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.ArrayList;


public class TitForTatRandom extends TitForTat {

	private final SecureRandom random = new SecureRandom();

    public TitForTatRandom(Decision lastOpponentMove) {
        super(lastOpponentMove);
    }

    public TitForTatRandom(ArrayList<Decision> opponentMoveHistoric) {
        super(opponentMoveHistoric);
    }

    @Override
    public Decision nextMove() {
        return random.nextDouble() < 0.1 ? random.nextBoolean() ? Decision.COOPERATE : Decision.BETRAY : super.nextMove();
    }
}
