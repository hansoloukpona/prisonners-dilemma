package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class TruePeacemaker implements Strategy {

    private SecureRandom random = new SecureRandom();

    private Decision lastButOneOpponentMove;

    private Decision lastOpponentMove;

    public TruePeacemaker(Decision lastButOneOpponentMove, Decision lastOpponentMove) {
        this.lastButOneOpponentMove = lastButOneOpponentMove;
        this.lastOpponentMove = lastOpponentMove;
    }

    // Stratégie 8: Vrai pacificateur
    @Override
    public Decision nextMove() {
        if (lastOpponentMove == Decision.BETRAY && lastButOneOpponentMove == Decision.BETRAY) {
            return random.nextDouble() < 0.1 ? Decision.COOPERATE : Decision.BETRAY;
        }
        return Decision.COOPERATE;
    }
}
