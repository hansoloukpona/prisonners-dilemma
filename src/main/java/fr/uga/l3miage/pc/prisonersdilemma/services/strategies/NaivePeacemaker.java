package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.ArrayList;


public class NaivePeacemaker implements Strategy {

    private final SecureRandom random = new SecureRandom();

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    public NaivePeacemaker(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public NaivePeacemaker(ArrayList<Decision> opponentMoveHistoric) {
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    // Strat�gie 7: Pacificateur na�f
    @Override
    public Decision nextMove() {
        chargeOpponentLastMoveFromHistoric();
        return random.nextDouble() < 0.1 ? Decision.COOPERATE : lastOpponentMove;
    }

    private void chargeOpponentLastMoveFromHistoric() {
        if (!opponentMoveHistoric.isEmpty()) {
            lastOpponentMove = getLastOpponentMove();
        } //On charge le dernier mouvement de l'adversaire si on a un acc�s interne � l'histotique de ses mouvements
    }

    public Decision getLastOpponentMove() {
        return getDecision(opponentMoveHistoric, 1);
    }
}
