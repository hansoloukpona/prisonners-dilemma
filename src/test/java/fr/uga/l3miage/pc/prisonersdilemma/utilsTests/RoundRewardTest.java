package fr.uga.l3miage.pc.prisonersdilemma.utilsTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.RoundReward;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoundRewardTest {

    @Test
    void testConstructor() {
        RoundReward roundReward = new RoundReward(10, 20);
        assertEquals(10, roundReward.getPlayer1Reward(), "Player 1 reward should be initialized correctly.");
        assertEquals(20, roundReward.getPlayer2Reward(), "Player 2 reward should be initialized correctly.");
    }

    @Test
    void testGetters() {
        RoundReward roundReward = new RoundReward(15, 25);
        assertEquals(15, roundReward.getPlayer1Reward(), "Getter for Player 1 reward should return correct value.");
        assertEquals(25, roundReward.getPlayer2Reward(), "Getter for Player 2 reward should return correct value.");
    }

    @Test
    void testSetters() {
        RoundReward roundReward = new RoundReward(0, 0);
        roundReward.setPlayer1Reward(30);
        roundReward.setPlayer2Reward(40);
        assertEquals(30, roundReward.getPlayer1Reward(), "Setter for Player 1 reward should update the value correctly.");
        assertEquals(40, roundReward.getPlayer2Reward(), "Setter for Player 2 reward should update the value correctly.");
    }
}

