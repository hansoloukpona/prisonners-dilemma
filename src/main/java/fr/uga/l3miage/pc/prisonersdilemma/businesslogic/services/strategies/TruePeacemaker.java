package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.security.SecureRandom;
import java.util.ArrayList;


public class TruePeacemaker implements Strategy {

    private final SecureRandom random = new SecureRandom();

    @Setter
    private Decision lastButOneOpponentMove;

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    public TruePeacemaker(Decision lastButOneOpponentMove, Decision lastOpponentMove) {
        this.lastButOneOpponentMove = lastButOneOpponentMove;
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public TruePeacemaker(ArrayList<Decision> opponentMoveHistoric) {
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    // Stratégie 8: Vrai pacificateur
    @Override
    public Decision nextMove() {
        chargeOpponentTwoLastMoveFromHistoric();
        if (lastOpponentMove == Decision.BETRAY && lastButOneOpponentMove == Decision.BETRAY) {
            return random.nextDouble() < 0.1 ? Decision.COOPERATE : Decision.BETRAY;
        }
        return Decision.COOPERATE;
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
