package fr.uga.l3miage.pc.prisonersdilemma.servicesTests;

import fr.uga.l3miage.pc.prisonersdilemma.services.Round;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

    @Test
    void testInitialization() {
        // Arrange & Act
        Round round = new Round(2);

        // Assert
        assertEquals(2, round.getChoiceFollower().getCount(), "ChoiceFollower should be initialized to 2");
        assertTrue(round.isReadyForPlayersChoices(), "ReadyForPlayersChoices should be true upon initialization");
    }

    @Test
    void testCountAPlayerChoice() throws InterruptedException {
        // Arrange
        Round round = new Round(2);

        // Act
        round.countAPlayerChoice();

        // Assert
        assertEquals(1, round.getChoiceFollower().getCount(), "ChoiceFollower count should decrement to 1");
        assertTrue(round.isReadyForPlayersChoices(), "ReadyForPlayersChoices should still be true");

        // Act
        round.countAPlayerChoice();

        // Assert
        assertEquals(0, round.getChoiceFollower().getCount(), "ChoiceFollower count should decrement to 0");
        assertFalse(round.isReadyForPlayersChoices(), "ReadyForPlayersChoices should be false when all choices are made");
    }

    @Test
    void testWaitForChoices() throws InterruptedException {
        // Arrange
        Round round = new Round(2);

        // Act
        Thread testThread = new Thread(() -> {
            try {
                round.waitForChoices();
            } catch (InterruptedException e) {
                fail("Thread was interrupted unexpectedly");
            }
        });

        testThread.start();

        // Make choices
        round.countAPlayerChoice();
        round.countAPlayerChoice();

        // Wait for the thread to finish
        testThread.join();

        // Assert
        assertFalse(testThread.isAlive(), "Thread should have completed waiting for choices");
    }

    @Test
    void testInterruptedExceptionHandlingInCountAPlayerChoice() {
        // Arrange
        Round round = new Round(2);

        try {
            // Act & Assert
            round.countAPlayerChoice();
            assertDoesNotThrow(() -> round.countAPlayerChoice(), "No InterruptedException should be thrown");
        } catch (Exception e) {
            fail("No exception should be thrown");
        }
    }

    @Test
    void testInterruptedExceptionHandlingInWaitForChoices() {
        // Arrange
        Round round = new Round(2);

        try {
            // Act & Assert
            round.countAPlayerChoice();
            round.countAPlayerChoice();
            assertDoesNotThrow(round::waitForChoices, "No InterruptedException should be thrown");
        } catch (Exception e) {
            fail("No exception should be thrown");
        }
    }
}

