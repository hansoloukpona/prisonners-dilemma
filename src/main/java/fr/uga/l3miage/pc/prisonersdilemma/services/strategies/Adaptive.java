package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Adaptive implements Strategy {

    private ArrayList<Decision> adaptiveDecisions;

    private ArrayList<Integer> scoresHistoric;

    public Adaptive(ArrayList<Integer> scoresHistoric) {
        this.scoresHistoric = scoresHistoric;
        chargeDecisionForAdaptiveInit();
    }

    // Stratégie 15: Adaptatif - Choisir la stratégie donnant le meilleur score
    @Override
    public Decision nextMove() {
        int cooperateScore = 0;
        int defectScore = 0;

        for (int i = 0; i < adaptiveDecisions.size(); i++) {

            if (i < 11) {
                return adaptiveDecisions.get(i);
            }
            if (adaptiveDecisions.get(i) == Decision.COOPERATE) {
                cooperateScore += scoresHistoric.get(i);
            } else {
                defectScore += scoresHistoric.get(i);
            }
        }
        Decision adaptiveDecision = (cooperateScore > defectScore) ? Decision.COOPERATE : Decision.BETRAY;
        adaptiveDecisions.add(adaptiveDecision);
        return adaptiveDecision;
    }

    private void chargeDecisionForAdaptiveInit(){

        this.adaptiveDecisions = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            this.adaptiveDecisions.add(Decision.COOPERATE);
        }

        for (int i = 0; i < 5; i++) {
            this.adaptiveDecisions.add(Decision.BETRAY);
        }
    }
}
