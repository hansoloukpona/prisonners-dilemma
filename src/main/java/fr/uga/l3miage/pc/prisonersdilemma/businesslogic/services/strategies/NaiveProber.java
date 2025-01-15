package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.ArrayList;


public class NaiveProber implements Strategy {

    private final SecureRandom random = new SecureRandom();

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    public NaiveProber(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public NaiveProber(ArrayList<Decision> opponentMoveHistoric) {
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    // Strat�gie 5: Sondeur na�f
    @Override
    public Decision nextMove() {
        chargeOpponentLastMoveFromHistoric();
        return random.nextDouble() < 0.1 ? Decision.BETRAY : lastOpponentMove;
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
