package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.util.ArrayList;


public class ForgivingGrudgerStrategy implements Strategy {

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    private boolean betrayed = false; // Indique si l'adversaire a déjà trahi
    private int punishmentStep = 0; // Étape actuelle de la punition

    public ForgivingGrudgerStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public ForgivingGrudgerStrategy(ArrayList<Decision> lastOpponentMoveHistoric) {
        this.opponentMoveHistoric = lastOpponentMoveHistoric;
    }

    @Override
    public Decision nextMove() {

        chargeOpponentLastMoveFromHistoric();

        if (lastOpponentMove == Decision.BETRAY) {
            betrayed = true;
        }

        if (betrayed) {
            // Définir la séquence de punition
            if (punishmentStep < 4) {
                punishmentStep++;
                return Decision.BETRAY; // Punir avec 4 trahisons
            } else if (punishmentStep < 6) {
                punishmentStep++;
                return Decision.COOPERATE; // Coopérer deux fois pour conclure la punition
            } else {
                punishmentStep = 0; // Réinitialiser après la punition
                betrayed = false; // Pardonner
            }
        }

        return Decision.COOPERATE; // Coopération par défaut
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

