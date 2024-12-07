package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;


public class SuspiciousTitForTatStrategy implements Strategy {

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    private boolean firstMove = true; // Indique si c'est le premier tour

    public SuspiciousTitForTatStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public SuspiciousTitForTatStrategy(ArrayList<Decision> opponentMoveHistoric) {
        this.opponentMoveHistoric = opponentMoveHistoric;
    }

    @Override
    public Decision nextMove() {
        if (firstMove) {
            firstMove = false;
            return Decision.BETRAY; // Commencer par trahir
        }
        chargeOpponentLastMoveFromHistoric();
        return lastOpponentMove; // Copier la décision de l'adversaire
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

