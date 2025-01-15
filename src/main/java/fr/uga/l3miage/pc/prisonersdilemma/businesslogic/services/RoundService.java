package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services;

public interface RoundService {
    void countAPlayerChoice() throws InterruptedException;

    void waitForChoices() throws InterruptedException;
}
