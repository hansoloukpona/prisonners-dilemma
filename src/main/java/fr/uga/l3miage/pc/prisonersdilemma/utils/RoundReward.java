package fr.uga.l3miage.pc.prisonersdilemma.utils;

//Business Logic

public class RoundReward {
    private int player1Reward;
    private int player2Reward;

    public RoundReward(int player1Reward, int player2Reward) {
        this.player1Reward = player1Reward;
        this.player2Reward = player2Reward;
    }

    public int getPlayer1Reward() {
        return player1Reward;
    }

    public void setPlayer1Reward(int player2Reward) {
        this.player1Reward = player2Reward;
    }

    public int getPlayer2Reward() {
        return player2Reward;
    }

    public void setPlayer2Reward(int player2Reward) {
        this.player2Reward = player2Reward;
    }
}
