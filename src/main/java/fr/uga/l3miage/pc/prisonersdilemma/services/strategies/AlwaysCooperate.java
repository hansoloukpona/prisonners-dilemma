package fr.uga.l3miage.pc.prisonersdilemma.services.strategies;

import lombok.Data;

@Data
public class AlwaysCooperate implements Strategy {

    public AlwaysCooperate() {
    }

    @Override
    public Decision nextMove() {
        return Decision.COOPERATE;
    }

}
