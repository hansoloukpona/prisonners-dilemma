package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.util.ArrayList;


public class ForgivingGrudgerStrategy implements Strategy {

    @Setter
    private Decision lastOpponentMove;

    private final ArrayList<Decision> opponentMoveHistoric;

    private boolean betrayed = false; // Indique si l'adversaire a d�j� trahi
    private int punishmentStep = 0; // �tape actuelle de la punition

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
            // D�finir la s�quence de punition
            if (punishmentStep < 4) {
                punishmentStep++;
                return Decision.BETRAY; // Punir avec 4 trahisons
            } else if (punishmentStep < 6) {
                punishmentStep++;
                return Decision.COOPERATE; // Coop�rer deux fois pour conclure la punition
            } else {
                punishmentStep = 0; // R�initialiser apr�s la punition
                betrayed = false; // Pardonner
            }
        }

        return Decision.COOPERATE; // Coop�ration par d�faut
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

