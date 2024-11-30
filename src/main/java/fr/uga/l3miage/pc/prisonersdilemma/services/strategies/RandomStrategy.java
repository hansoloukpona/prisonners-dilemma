package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class RandomStrategy implements Strategy {

    private SecureRandom random = new SecureRandom();

    public RandomStrategy() {
    }

    @Override
    public Decision nextMove() {
        return random.nextBoolean() ? Decision.COOPERATE : Decision.BETRAY;
    }

}
