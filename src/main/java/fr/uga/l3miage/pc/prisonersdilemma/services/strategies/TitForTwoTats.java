package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class TitForTwoTats implements Strategy {

    private SecureRandom random = new SecureRandom();

    private Decision lastButOneOpponentMove;

    private Decision lastOpponentMove;

    public TitForTwoTats(Decision lastButOneOpponentMove, Decision lastOpponentMove) {
        this.lastButOneOpponentMove = lastButOneOpponentMove;
        this.lastOpponentMove = lastOpponentMove;
    }

    // Stratégie 4: Donnant pour deux donnants
    @Override
    public Decision nextMove() {
        return (lastOpponentMove == lastButOneOpponentMove) ? lastOpponentMove : Decision.COOPERATE;
    }

}
