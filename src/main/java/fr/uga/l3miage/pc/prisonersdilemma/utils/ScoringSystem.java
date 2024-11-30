package fr.uga.l3miage.pc.prisonersdilemma.utils;

import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Decision;

public class ScoringSystem {
    private static final int T = 5;  // Trahir gain
    private static final int D = 0;  // Duper perte
    private static final int C = 3;  // Coopérer gain
    private static final int P = 1;  // Mutuelle trahison perte

    public static RoundReward calculateScore(Decision playerDecision, Decision opponentDecision) {
        if (playerDecision == Decision.BETRAY && opponentDecision == Decision.COOPERATE) {
            return new RoundReward(T,D);
        } else if (playerDecision == Decision.COOPERATE && opponentDecision == Decision.BETRAY) {
            return new RoundReward(D,T);
        } else if (playerDecision == Decision.COOPERATE && opponentDecision == Decision.COOPERATE) {
            return new RoundReward(C,C);
        } else if (playerDecision == Decision.BETRAY && opponentDecision == Decision.BETRAY) {
            return new RoundReward(P,P);
        } else {
            return new RoundReward(D,D);
        }
    }
}
