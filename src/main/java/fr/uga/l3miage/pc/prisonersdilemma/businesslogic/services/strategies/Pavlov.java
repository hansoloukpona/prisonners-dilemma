package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Setter;

import java.util.ArrayList;


public class Pavlov implements Strategy {

    private int myLastScore;

    @Setter
    private Decision myLastMove;

    private final ArrayList<Integer> myScoresHistoric;

    private final ArrayList<Decision> myMoveHistoric;

    public Pavlov(int lastScore, Decision myLastMove) {
        this.myLastScore = lastScore;
        this.myLastMove = myLastMove;
        this.myScoresHistoric = new ArrayList<>();
        this.myMoveHistoric = new ArrayList<>();
    }

    public Pavlov(ArrayList<Integer> myScoresHistoric, ArrayList<Decision> myMoveHistoric) {
        this.myScoresHistoric = myScoresHistoric;
        this.myMoveHistoric = myMoveHistoric;
    }

    // Stratégie 13: Pavlov - Si 5 ou 3 points obtenus au tour précédent, répéter le dernier choix
    @Override
    public Decision nextMove() {

        chargeMyLastScoreFromHistoric();
        chargeMyLastMoveFromHistoric();

        if (myLastScore == 3 || myLastScore == 5) {
            return myLastMove;
        }
        return myLastMove == Decision.COOPERATE ? Decision.BETRAY : Decision.COOPERATE;
    }

    private void chargeMyLastMoveFromHistoric() {
        if (!myMoveHistoric.isEmpty()) {
            myLastMove = getMyLastMove();
        } //On charge le dernier mouvement de l'adversaire si on a un accès interne à l'histotique de ses mouvements
    }

    public Decision getMyLastMove() {
        return getDecision(myMoveHistoric, 1);
    }

    private void chargeMyLastScoreFromHistoric() {
        if (!myScoresHistoric.isEmpty()) {
            myLastScore = getMyLastScore();
        } //On charge le dernier mouvement de l'adversaire si on a un accès interne à l'histotique de ses mouvements
    }

    public int getMyLastScore() {
        return getScore(myScoresHistoric, 1);
    }

}
