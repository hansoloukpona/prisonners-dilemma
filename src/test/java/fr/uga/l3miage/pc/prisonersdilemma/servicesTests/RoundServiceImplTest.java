package fr.uga.l3miage.pc.prisonersdilemma.servicesTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.RoundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundServiceImplTest {

    private RoundServiceImpl roundService;

    @BeforeEach
    void setUp() {
        // Initialisation avec un seul joueur pour tester le comportement de CountDownLatch
        roundService = new RoundServiceImpl(1);
    }

    @Test
    void testCountAPlayerChoice_WhenCalled_DecrementsCount() throws InterruptedException {
        // Act
        roundService.countAPlayerChoice();

        // Assert
        // Vérifie que le compte du CountDownLatch est décrémenté à 0
        assertEquals(0, roundService.getChoiceFollower().getCount());
        assertFalse(roundService.isReadyForPlayersChoices());
    }

    @Test
    void testCountAPlayerChoice_WhenCalledAndCountIsZero_SetsReadyForPlayersChoicesToFalse() throws InterruptedException {
        // Act
        roundService.countAPlayerChoice();

        // Assert
        // Vérifie que le flag readyForPlayersChoices est mis à false après que CountDownLatch atteint 0
        assertFalse(roundService.isReadyForPlayersChoices());
    }

    @Test
    void testWaitForChoices_WhenCalled_AwaitsUntilCountDown() throws InterruptedException {
        // Arrange
        Thread waitThread = new Thread(() -> {
            try {
                roundService.waitForChoices();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Act
        waitThread.start();
        // Simule une action de joueur pour diminuer le CountDownLatch
        roundService.countAPlayerChoice();

        // Attend la fin du thread
        waitThread.join();

        // Assert
        // Vérifie que le thread de "waitForChoices" a bien terminé son attente
        assertEquals(0, roundService.getChoiceFollower().getCount());
    }

    @Test
    void testWaitForChoices_WhenCountIsZero_ReturnsImmediately() throws InterruptedException {
        // Arrange
        roundService.countAPlayerChoice(); // Le CountDownLatch est déjà à 0

        // Act
        long startTime = System.currentTimeMillis();
        roundService.waitForChoices();
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Assert
        // Vérifie que le temps écoulé est très court, car il ne devait pas attendre
        assertTrue(elapsedTime < 50);
    }

    @Test
    void testConstructor_InitializesCountDownLatch() {
        // Arrange
        RoundServiceImpl roundServiceWithTwoPlayers = new RoundServiceImpl(2);

        // Assert
        // Vérifie que le CountDownLatch est initialisé avec la valeur correcte
        assertEquals(2, roundServiceWithTwoPlayers.getChoiceFollower().getCount());
    }
}