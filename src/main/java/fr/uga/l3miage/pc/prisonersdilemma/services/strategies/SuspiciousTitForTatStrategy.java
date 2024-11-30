package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

@Data
public class SuspiciousTitForTatStrategy implements Strategy {

    private Decision lastOpponentMove;

    private boolean firstMove = true; // Indique si c'est le premier tour

    public SuspiciousTitForTatStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    @Override
    public Decision nextMove() {
        if (firstMove) {
            firstMove = false;
            return Decision.BETRAY; // Commencer par trahir
        }
        return lastOpponentMove; // Copier la décision de l'adversaire
    }
}

