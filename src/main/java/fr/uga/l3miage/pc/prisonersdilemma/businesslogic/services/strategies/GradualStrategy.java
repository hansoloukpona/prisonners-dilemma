package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.util.ArrayList;


public class GradualStrategy implements Strategy {

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    private int opponentBetrayCount = 0; // Nombre total de trahisons de l'adversaire
    private int punishmentTurnsLeft = 0; // Nombre de trahisons restantes pour la punition
    private int postPunishmentCooperations = 0; // Nombre de coop�rations apr�s punition

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
            punishmentTurnsLeft = opponentBetrayCount; // R�initialise la punition
            postPunishmentCooperations = 0; // R�initialise les coop�rations apr�s punition
        }

        if (punishmentTurnsLeft > 0) {
            punishmentTurnsLeft--;
            return Decision.BETRAY; // Effectuer la punition (trahisons)
        }

        if (postPunishmentCooperations < 2) {
            postPunishmentCooperations++;
            return Decision.COOPERATE; // Coop�rer deux fois apr�s la punition
        }

        return Decision.COOPERATE; // Coop�rer par d�faut en dehors des cas sp�ciaux
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

