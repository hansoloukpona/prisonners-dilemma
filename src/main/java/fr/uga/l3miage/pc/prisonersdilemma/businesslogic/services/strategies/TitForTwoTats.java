package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.ArrayList;


public class TitForTwoTats implements Strategy {

    private final SecureRandom random = new SecureRandom();

    @Setter
    private Decision lastButOneOpponentMove;

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    public TitForTwoTats(Decision lastButOneOpponentMove, Decision lastOpponentMove) {
        this.lastButOneOpponentMove = lastButOneOpponentMove;
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public TitForTwoTats(ArrayList<Decision> opponentMoveHistoric) {
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    // Stratégie 4: Donnant pour deux donnants
    @Override
    public Decision nextMove() {
        chargeOpponentTwoLastMoveFromHistoric();
        return (lastOpponentMove == lastButOneOpponentMove) ? lastOpponentMove : Decision.COOPERATE;
    }

    private void chargeOpponentTwoLastMoveFromHistoric() {
        if (!opponentMoveHistoric.isEmpty()) {
            lastOpponentMove = getLastOpponentMove();
            lastButOneOpponentMove = getLastButOneOpponentMove();
        } //On charge les 2 derniers mouvements de l'adversaire si on a un accès interne à l'histotique de ses mouvements
    }

    public Decision getLastOpponentMove() {
        return getDecision(opponentMoveHistoric, 1);
    }

    public Decision getLastButOneOpponentMove() {
        return getDecision(opponentMoveHistoric, 2);
    }

}
