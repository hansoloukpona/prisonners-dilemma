package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

//Business Logic

@Data
public class RoundServiceImpl implements RoundService {

    @JsonIgnore
    private CountDownLatch choiceFollower;

    private boolean readyForPlayersChoices;

    public RoundServiceImpl(int count) {
        // count ne doit en tout cas pas être supérieur à 2.
        this.choiceFollower = new CountDownLatch(count);
        this.readyForPlayersChoices = true;
    }

    @Override
    public void countAPlayerChoice() throws InterruptedException {
        choiceFollower.countDown();
        if (choiceFollower.getCount() == 0) readyForPlayersChoices = false;
    }

    @Override
    public void waitForChoices() throws InterruptedException {
        choiceFollower.await();
    }

}

