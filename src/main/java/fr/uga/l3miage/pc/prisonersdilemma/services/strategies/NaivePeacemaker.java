package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class NaivePeacemaker implements Strategy {

    private SecureRandom random = new SecureRandom();

    private Decision lastOpponentMove;

    public NaivePeacemaker(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    // Strat�gie 7: Pacificateur na�f
    @Override
    public Decision nextMove() {
        return random.nextDouble() < 0.1 ? Decision.COOPERATE : lastOpponentMove;
    }
}
