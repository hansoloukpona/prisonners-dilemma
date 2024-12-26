package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import java.util.ArrayList;
import java.util.Random;

//Business Logic (et tout ce qui en hérite)

public interface Strategy {

     Decision nextMove();

     public default Decision getDecision(ArrayList<Decision> moveHistoric, int index) {
          return moveHistoric.get(moveHistoric.size() - index);
     }

     public default int getScore(ArrayList<Integer> scoreHistoric, int index) {
          return scoreHistoric.get(scoreHistoric.size() - index);
     }

     /*public default Decision getHistoricMove(ArrayList<Decision> moveHistoric, int index) {
          return getDecision(moveHistoric, index);
     }*/

     // Helper method to return a random move
     public default Decision randomMove() {
          return new Random().nextBoolean() ? Decision.COOPERATE : Decision.BETRAY;
     }

}