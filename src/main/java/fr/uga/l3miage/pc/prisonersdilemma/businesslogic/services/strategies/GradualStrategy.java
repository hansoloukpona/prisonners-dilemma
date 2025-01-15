package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.util.ArrayList;


public class GradualStrategy implements Strategy {

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    private int opponentBetrayCount = 0; // Nombre total de trahisons de l'adversaire
    private int punishmentTurnsLeft = 0; // Nombre de trahisons restantes pour la punition
    private int postPunishmentCooperations = 0; // Nombre de coopérations après punition

    public GradualStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
        this.opponentMoveHistoric = new ArrayList<>();
    }

    public GradualStrategy(ArrayList<Decision> lastOpponentMoveHistoric) {
        this.opponentMoveHistoric = lastOpponentMoveHistoric;
    }

    @Override
    public Decision nextMove() {

        chargeOpponentLastMoveFromHistoric();

        if (lastOpponentMove == Decision.BETRAY) {
            opponentBetrayCount++;
            punishmentTurnsLeft = opponentBetrayCount; // Réinitialise la punition
            postPunishmentCooperations = 0; // Réinitialise les coopérations après punition
        }

        if (punishmentTurnsLeft > 0) {
            punishmentTurnsLeft--;
            return Decision.BETRAY; // Effectuer la punition (trahisons)
        }

        if (postPunishmentCooperations < 2) {
            postPunishmentCooperations++;
            return Decision.COOPERATE; // Coopérer deux fois après la punition
        }

        return Decision.COOPERATE; // Coopérer par défaut en dehors des cas spéciaux
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

