package fr.uga.l3miage.pc.prisonersdilemma.utilsTests;


import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Decision;
import fr.uga.l3miage.pc.prisonersdilemma.utils.RoundReward;
import fr.uga.l3miage.pc.prisonersdilemma.utils.ScoringSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringSystemTest {

    @Test
    void testBetrayVsCooperate() {
        RoundReward reward = ScoringSystem.calculateScore(Decision.BETRAY, Decision.COOPERATE);
        assertEquals(5, reward.getPlayer1Reward());
        assertEquals(0, reward.getPlayer2Reward());
    }

    @Test
    void testCooperateVsBetray() {
        RoundReward reward = ScoringSystem.calculateScore(Decision.COOPERATE, Decision.BETRAY);
        assertEquals(0, reward.getPlayer1Reward());
        assertEquals(5, reward.getPlayer2Reward());
    }

    @Test
    void testCooperateVsCooperate() {
        RoundReward reward = ScoringSystem.calculateScore(Decision.COOPERATE, Decision.COOPERATE);
        assertEquals(3, reward.getPlayer1Reward());
        assertEquals(3, reward.getPlayer2Reward());
    }

    @Test
    void testBetrayVsBetray() {
        RoundReward reward = ScoringSystem.calculateScore(Decision.BETRAY, Decision.BETRAY);
        assertEquals(1, reward.getPlayer1Reward());
        assertEquals(1, reward.getPlayer2Reward());
    }

    @Test
    void testNullDecisions() {
        // Ajouter ce test si les décisions nulles sont possibles
        RoundReward reward = ScoringSystem.calculateScore(null, null);
        assertEquals(0, reward.getPlayer1Reward());
        assertEquals(0, reward.getPlayer2Reward());
    }
}

