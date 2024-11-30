package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

@Data
public class ForgivingGrudgerStrategy implements Strategy {

    private Decision lastOpponentMove;

    private boolean betrayed = false; // Indique si l'adversaire a déjà trahi
    private int punishmentStep = 0; // Étape actuelle de la punition

    public ForgivingGrudgerStrategy(Decision lastOpponentMove) {
        this.lastOpponentMove = lastOpponentMove;
    }

    @Override
    public Decision nextMove() {
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
}

