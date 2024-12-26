package fr.uga.l3miage.pc.prisonersdilemma.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CountDownLatch;

//Business Logic

@Data
public class Round {

    @JsonIgnore
    private CountDownLatch choiceFollower;

    private boolean readyForPlayersChoices;

    public Round(int count) {
        // count ne doit en tout cas pas être supérieur à 2
        this.choiceFollower = new CountDownLatch(count);
        this.readyForPlayersChoices = true;
    }

    public void countAPlayerChoice() throws InterruptedException {
        choiceFollower.countDown();
        if (choiceFollower.getCount() == 0) readyForPlayersChoices = false;
    }

    public void waitForChoices() throws InterruptedException {
        choiceFollower.await();
    }

}

