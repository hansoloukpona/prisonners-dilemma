package fr.uga.l3miage.pc.prisonersdilemma.servicesTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceImplTest {

    private GameServiceImpl gameService;
    private Player player1;
    private Player player2;
    private Logger logger;

    @BeforeEach
    void setUp() {
        // Initialisation des objets
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        logger = mock(Logger.class);

        gameService = new GameServiceImpl();
    }

    @Test
    void testPlayerIsPresentInTheGame_WhenPlayerIsNull() {
        // Act
        boolean result = gameService.playerIsPresentInTheGame(null);

        // Assert
        assertFalse(result);
        verify(logger).info("We need a second player, of course! Come on, try again!");
    }

    @Test
    void testPlayerIsPresentInTheGame_WhenPlayerIsNotConnected() {
        // Arrange
        when(player1.getConnected()).thenReturn(false);

        // Act
        boolean result = gameService.playerIsPresentInTheGame(player1);

        // Assert
        assertFalse(result);
        verify(logger).info("We need a second player, of course! Come on, try again!");
    }

    @Test
    void testPlayerIsPresentInTheGame_WhenPlayerIsConnected() {
        // Arrange
        when(player1.getConnected()).thenReturn(true);

        // Act
        boolean result = gameService.playerIsPresentInTheGame(player1);

        // Assert
        assertTrue(result);
    }

    @Test
    void testDecisionIsValid_WhenDecisionIsCooperate() {
        // Act
        boolean result = gameService.decisionIsValid("COOPERATE");

        // Assert
        assertTrue(result);
    }

    @Test
    void testDecisionIsValid_WhenDecisionIsBetray() {
        // Act
        boolean result = gameService.decisionIsValid("BETRAY");

        // Assert
        assertTrue(result);
    }

    @Test
    void testDecisionIsValid_WhenDecisionIsInvalid() {
        // Act
        boolean result = gameService.decisionIsValid("INVALID_DECISION");

        // Assert
        assertFalse(result);
    }

    // Ajouter un test pour la méthode commentée si elle est réintégrée

    /*@Test
    void testUserExistAndActiveInGame() throws Exception {
        // Arrange
        UUID playerId = UUID.randomUUID();
        when(player1.getPlayerId()).thenReturn(playerId);
        when(player1.getConnected()).thenReturn(true);
        when(player2.getConnected()).thenReturn(false);

        // Act & Assert
        gameService.userExistAndActiveInGame(playerId, player1, player2);  // ne doit pas lancer d'exception
    }*/

}