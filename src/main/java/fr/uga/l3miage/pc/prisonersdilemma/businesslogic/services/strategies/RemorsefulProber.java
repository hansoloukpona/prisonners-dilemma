package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.ArrayList;


public class RemorsefulProber implements Strategy {

    private final SecureRandom random = new SecureRandom();

    @Setter
    private Decision lastOpponentMove;

    @Setter
    private Decision myLastMove;

    private final ArrayList<Decision> myMoveHistoric;

    private final ArrayList<Decision> opponentMoveHistoric;

    public RemorsefulProber(Decision lastOpponentMove, Decision myLastMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.myLastMove = myLastMove;
        this.myMoveHistoric = new ArrayList<>();
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public RemorsefulProber(ArrayList<Decision> myMoveHistoric, ArrayList<Decision> opponentMoveHistoric) {
        this.myMoveHistoric = myMoveHistoric;
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    // Stratégie 6: Sondeur repentant
    @Override
    public Decision nextMove() {

        chargeMyLastMoveFromHistoric();
        chargeOpponentLastMoveFromHistoric();

        if (random.nextDouble() < 0.1) {
            return Decision.BETRAY;
        }

        if (lastOpponentMove == Decision.BETRAY && myLastMove == Decision.BETRAY) {
            return Decision.COOPERATE;
        }
        return lastOpponentMove;
    }

    private void chargeMyLastMoveFromHistoric() {
        if (!myMoveHistoric.isEmpty()) {
            myLastMove = getMyLastMove();
        } //On charge le dernier mouvement de l'adversaire si on a un accès interne à l'histotique de ses mouvements
    }

    public Decision getMyLastMove() {
        return getDecision(myMoveHistoric, 1);
    }

    private void chargeOpponentLastMoveFromHistoric() {
        if (!opponentMoveHistoric.isEmpty()) {
            lastOpponentMove = getLastOpponentMove();
        } //On charge le dernier mouvement de l'adversaire si on a un accès interne à l'histotique de ses mouvements
    }

    public Decision getLastOpponentMove() {
        return getDecision(opponentMoveHistoric, 1);
    }
}
