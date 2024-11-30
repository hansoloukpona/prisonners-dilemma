package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

@Data
public class GradualStrategy implements Strategy {

    private Decision lastOpponentMove;

    private int opponentBetrayCount = 0; // Nombre total de trahisons de l'adversaire
    private int punishmentTurnsLeft = 0; // Nombre de trahisons restantes pour la punition
    private int postPunishmentCooperations = 0; // Nombre de coopérations après punition

    public GradualStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    @Override
    public Decision nextMove() {
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
}

