package fr.uga.l3miage.pc.prisonersdilemma.severside.adaptation;

import fr.uga.l3miage.pc.enums.EnumIdJoueur;
import fr.uga.l3miage.pc.models.Tour;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Decision;
import fr.uga.l3miage.pc.strategies.SimpleStrategy;
import lombok.Data;

//Server-Side

@Data
public class FromGroup1_7ToGroup2_6StrategiesUsage {

    private SimpleStrategy strategy;

    private Tour[] tourHistoric;

    private final String player1;

    private final String player2;

    public FromGroup1_7ToGroup2_6StrategiesUsage(SimpleStrategy strategy, Player thePlayer1, Player thePlayer2, int totalRoundNumber) {
        this.strategy = strategy;
        this.tourHistoric = new Tour[totalRoundNumber];
        ChargeDataToPlayTheRound(thePlayer1, thePlayer2);
        this.player1 = thePlayer1.getPlayerId().toString();
        this.player2 = thePlayer2.getPlayerId().toString();

    }

    private void ChargeDataToPlayTheRound(Player thePlayer1, Player thePlayer2) {
        for (int i = 0; i < thePlayer1.getPlayerDecisionsHistoric().size(); i++) {
            tourHistoric[i] = new Tour(convertDecisionToBoolean(thePlayer1.getPlayerDecisionsHistoric().get(i)), convertDecisionToBoolean(thePlayer2.getPlayerDecisionsHistoric().get(i)));
        }
    }

    private void ChargeDataToPlayTheRound(Player thePlayer1, Player thePlayer2, int playedRoundNumber) {
        for (int i = 0; i < playedRoundNumber; i++) {
            tourHistoric[i] = updateDataToPlayTheRound(thePlayer1.getPlayerDecisionsHistoric().get(i), thePlayer2.getPlayerDecisionsHistoric().get(i));
        }
    }

    private Boolean convertDecisionToBoolean(Decision thePlayerDecision) {
        return thePlayerDecision != Decision.BETRAY;
    }

    private Tour updateDataToPlayTheRound(Decision thePlayer1Decision, Decision thePlayer2Decision) {
        return new Tour(convertDecisionToBoolean(thePlayer1Decision), convertDecisionToBoolean(thePlayer2Decision));
    }

    private Tour updateTourHistoricToPlayTheRound(Player thePlayer1, Player thePlayer2) {
        return new Tour(convertDecisionToBoolean(getLastDecision(thePlayer1)), convertDecisionToBoolean(getLastDecision(thePlayer2)));
    }

    private Decision getLastDecision(Player thePlayer) {
        return thePlayer.getPlayerDecisionsHistoric().get(thePlayer.getPlayerDecisionsHistoric().size() -1);
    }

    public Decision play(Player thePlayer1, Player thePlayer2, int playedRoundNumber, String playerSupplyedIdString) {
        tourHistoric[playedRoundNumber-1] = updateTourHistoricToPlayTheRound(thePlayer1, thePlayer2);
        if (!strategy.doStrategy(tourHistoric, verifyThePlayerId(playerSupplyedIdString))) {
            return Decision.BETRAY;
        } else {
            return Decision.COOPERATE;
        }
    }

    public EnumIdJoueur verifyThePlayerId(String playerIdString) {
        if (this.player1.equals(playerIdString)) {
            return EnumIdJoueur.TINTIN;
        } else {
            return EnumIdJoueur.MILOU;
        }
    }

    public EnumIdJoueur getThePlayer2Id() {
        return EnumIdJoueur.MILOU;
    }

}
