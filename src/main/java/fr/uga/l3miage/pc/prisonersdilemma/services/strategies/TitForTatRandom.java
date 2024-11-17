package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import fr.uga.l3miage.pc.prisonersdilemma.services.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.utils.Decision;

import java.security.SecureRandom;


public class TitForTatRandom implements Strategy {
	
	

	private SecureRandom random = new SecureRandom();


    @Override
    public Decision nextMove(Decision lastOpponentMove) {
        return random.nextDouble() < 0.1 ? random.nextBoolean() ? Decision.COOPERATE : Decision.BETRAY : lastOpponentMove;
    }
}
