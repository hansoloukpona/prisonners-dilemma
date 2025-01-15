package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import java.security.SecureRandom;
import java.util.ArrayList;


public class TitForTwoTatsRandom extends TitForTwoTats {

    private final SecureRandom random = new SecureRandom();

    public TitForTwoTatsRandom(Decision lastButOneOpponentMove, Decision lastOpponentMove) {
        super(lastButOneOpponentMove, lastOpponentMove);
    }

    public TitForTwoTatsRandom(ArrayList<Decision> opponentMoveHistoric) {
        super(opponentMoveHistoric);
    }

    // Strat�gie 3: Donnant pour deux donnants et al�atoire
    @Override
    public Decision nextMove() {

        /*if (opponentMoveHistoric != null && opponentMoveHistoric.size() >= 2) {
        }*/
        if (random.nextDouble() < 0.1) {
            return randomMove();
        }
        return super.nextMove();
    }

}
