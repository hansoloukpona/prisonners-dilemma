package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

@Data
public class GradualStrategy implements Strategy {

    private Decision lastOpponentMove;

    private int opponentBetrayCount = 0; // Nombre total de trahisons de l'adversaire
    private int punishmentTurnsLeft = 0; // Nombre de trahisons restantes pour la punition
    private int postPunishmentCooperations = 0; // Nombre de coop�rations apr�s punition

    public GradualStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    @Override
    public Decision nextMove() {
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
}

