package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;


@Data
public class TitForTwoTatsRandom implements Strategy {

    private SecureRandom random = new SecureRandom();

    private Decision lastButOneOpponentMove;

    private Decision lastOpponentMove;

    public TitForTwoTatsRandom(Decision lastButOneOpponentMove, Decision lastOpponentMove) {
        this.lastButOneOpponentMove = lastButOneOpponentMove;
        this.lastOpponentMove = lastOpponentMove;
    }

    // Stratégie 3: Donnant pour deux donnants et aléatoire
    @Override
    public Decision nextMove() {

        /*if (opponentMoveHistoric != null && opponentMoveHistoric.size() >= 2) {
        }*/
        if (random.nextDouble() < 0.1) {
            return randomMove();
        }
        return (lastOpponentMove == lastButOneOpponentMove) ? lastOpponentMove : Decision.COOPERATE;
    }

}
