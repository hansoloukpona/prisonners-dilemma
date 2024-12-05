package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import java.util.ArrayList;
import java.util.Random;

public interface Strategy {

     Decision nextMove();

     public default Decision getDecision(ArrayList<Decision> opponentMoveHistoric, int index) {
          return opponentMoveHistoric.get(opponentMoveHistoric.size() - index);
     }

     public default Decision getHistoricMove(ArrayList<Decision> moveHistoric, int index) {
          return getDecision(moveHistoric, index);
     }

    public default void updateContext() {

    }

    // Helper method to return a random move
     public default Decision randomMove() {
          return new Random().nextBoolean() ? Decision.COOPERATE : Decision.BETRAY;
     }

}