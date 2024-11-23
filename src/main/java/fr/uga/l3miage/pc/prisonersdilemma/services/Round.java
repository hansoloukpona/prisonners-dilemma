package fr.uga.l3miage.pc.prisonersdilemma.services;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CountDownLatch;


@Data
public class Round {

    private CountDownLatch choiceFollower;
    private boolean readyForPlayersChoices;

    public Round() {
        this.choiceFollower = new CountDownLatch(2);
        this.readyForPlayersChoices = false;
    }

    public void countAPlayerChoice() throws InterruptedException {
        choiceFollower.countDown();
    }

    public void waitForChoices() throws InterruptedException {
        choiceFollower.await();
    }

}

