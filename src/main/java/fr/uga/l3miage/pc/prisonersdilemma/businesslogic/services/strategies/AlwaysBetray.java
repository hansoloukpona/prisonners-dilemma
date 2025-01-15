package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies;

import lombok.Data;

@Data
public class AlwaysBetray implements Strategy {

    public AlwaysBetray() {
    }

    // Stratégie 10: Toujours trahir
    @Override
    public Decision nextMove() {
        return Decision.BETRAY;
    }
}
