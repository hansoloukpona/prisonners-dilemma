package fr.uga.l3miage.pc.prisonersdilemma.services;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CountDownLatch;


@Data
public class Round {

    private CountDownLatch choiceFollower;
    private boolean readyForPlayersChoices;
    private CountDownLatch roundResultConsultationFollower;
    private boolean readyForRoundResultConsultation;
    private CountDownLatch resultConsultationFollower;
    private boolean readyForResultConsultation;
    private String resulDataCompilation;

    public Round() {
        this.choiceFollower = new CountDownLatch(2);
        this.roundResultConsultationFollower = new CountDownLatch(2);
        this.readyForRoundResultConsultation = false;
        this.readyForResultConsultation = false;
        this.readyForPlayersChoices = false;
    }

    public void countAPlayerChoice() throws InterruptedException {
        choiceFollower.countDown();  // Décrémente le countDown pour signaler que le joueur 1 a fait son choix
        if (choiceFollower.getCount() == 0)
            readyForRoundResultConsultation = false;
    }

    public void waitForChoices() throws InterruptedException {
        choiceFollower.await();  // Attend que les deux joueurs aient fait leurs choix
        readyForRoundResultConsultation = true;
    }

    public void countARoundResultConsultation() throws InterruptedException {
        roundResultConsultationFollower.countDown();
        if (roundResultConsultationFollower.getCount() == 0)
            readyForRoundResultConsultation = false;
    }

    public void waitForRoundResultConsultation() throws InterruptedException {
        roundResultConsultationFollower.await();
        readyForRoundResultConsultation = true;
    }

    public void initTheGameResultConsultation() throws InterruptedException {
        this.resultConsultationFollower = new CountDownLatch(2);
    }

    public void countTheGameResultConsultation() throws InterruptedException {
        resultConsultationFollower.countDown();
        if (resultConsultationFollower.getCount() == 0)
            readyForResultConsultation = false;
    }

    public void waitForTheGameResultConsultation() throws InterruptedException {
        resultConsultationFollower.await();
        readyForResultConsultation = true;
    }
}

