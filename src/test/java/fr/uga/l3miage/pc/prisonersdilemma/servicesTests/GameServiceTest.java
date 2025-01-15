package fr.uga.l3miage.pc.prisonersdilemma.servicesTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameService;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl();
    }

    @Test
    void testPlayerIsPresentInTheGame_PlayerConnected() {
        // Arrange
        Player player = mock(Player.class);
        when(player.getConnected()).thenReturn(true);

        // Act
        boolean result = gameService.playerIsPresentInTheGame(player);

        // Assert
        assertTrue(result, "Expected playerIsPresentInTheGame to return true for a connected player");
    }

    @Test
    void testPlayerIsPresentInTheGame_PlayerNotConnected() {
        // Arrange
        Player player = mock(Player.class);
        when(player.getConnected()).thenReturn(false);

        // Act
        boolean result = gameService.playerIsPresentInTheGame(player);

        // Assert
        assertFalse(result, "Expected playerIsPresentInTheGame to return false for a non-connected player");
    }

    @Test
    void testPlayerIsPresentInTheGame_PlayerNull() {
        // Act
        boolean result = gameService.playerIsPresentInTheGame(null);

        // Assert
        assertFalse(result, "Expected playerIsPresentInTheGame to return false for a null player");
    }

    @Test
    void testDecisionIsValid_ValidDecisions() {
        //assertTrue(GameService.decisionIsValid("COOPERATE"), "Expected decisionIsValid to return true for 'COOPERATE'");
        //assertTrue(GameService.decisionIsValid("BETRAY"), "Expected decisionIsValid to return true for 'BETRAY'");
    }

    @Test
    void testDecisionIsValid_InvalidDecision() {
        //assertFalse(GameService.decisionIsValid("INVALID"), "Expected decisionIsValid to return false for an invalid decision");
    }

    //TODO
    /*@Test
    void testVerifyPlayer_ValidPlayer() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getPlayerId()).thenReturn(playerId);
        when(player.getConnected()).thenReturn(true);

        // Act
        boolean result = gameService.verifyPlayer(playerId, player);

        // Assert
        assertTrue(result, "Expected verifyPlayer to return true for a valid, connected player");
    }

    @Test
    void testVerifyPlayer_InvalidPlayerId() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        UUID differentPlayerId = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getPlayerId()).thenReturn(differentPlayerId);
        when(player.getConnected()).thenReturn(true);

        // Act
        boolean result = gameService.verifyPlayer(playerId, player);

        // Assert
        assertFalse(result, "Expected verifyPlayer to return false for a player with a different ID");
    }

    @Test
    void testVerifyPlayer_PlayerNotConnected() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getPlayerId()).thenReturn(playerId);
        when(player.getConnected()).thenReturn(false);

        // Act
        boolean result = gameService.verifyPlayer(playerId, player);

        // Assert
        assertFalse(result, "Expected verifyPlayer to return false for a disconnected player");
    }*/
}

