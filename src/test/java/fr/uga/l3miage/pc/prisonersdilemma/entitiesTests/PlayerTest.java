package fr.uga.l3miage.pc.prisonersdilemma.entitiesTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Decision;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Player player;
    private Strategy mockStrategy;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(Strategy.class);
        player = new Player("TestPlayer");
        player.setStrategy(mockStrategy);
    }

    @Test
    void testPlay_withStrategy() {
        // Arrange
        when(mockStrategy.nextMove()).thenReturn(Decision.BETRAY);

        // Act
        Decision decision = player.play();

        // Assert
        assertEquals(Decision.BETRAY, decision);
        verify(mockStrategy, times(1)).nextMove();
    }

    @Test
    void testPlay_withoutStrategy() {
        // Arrange
        player.setStrategy(null);

        // Act
        Decision decision = player.play();

        // Assert
        assertEquals(Decision.COOPERATE, decision);
    }

    @Test
    void testSendToPlayer_whenConnected() {
        // Arrange
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        Object message = "TestMessage";
        player.setConnected(true);
        player.setPlayerSessionId("session-id");

        // Act
        player.sendToPlayer(simpMessagingTemplate, message);

        // Assert
        //verify(simpMessagingTemplate, times(1)).convertAndSendToUser(eq("session-id"), any(), eq(message));
    }

    @Test
    void testSendToPlayer_whenNotConnected() {
        // Arrange
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        Object message = "TestMessage";
        player.setConnected(false);

        // Act
        player.sendToPlayer(simpMessagingTemplate, message);

        // Assert
        verify(simpMessagingTemplate, never()).convertAndSendToUser(any(), any(), any());
    }

    @Test
    void testGiveUp() {
        // Arrange
        Strategy newStrategy = mock(Strategy.class);

        // Act
        player.giveUp(newStrategy);

        // Assert
        assertFalse(player.isConnected());
        assertEquals(newStrategy, player.getStrategy());
    }

    @Test
    void testUpdateDatas() {
        // Arrange
        player.setActualRoundDecision(Decision.COOPERATE);
        int initialScore = player.getScore();
        int points = 5;

        // Act
        player.updateDatas(points);

        // Assert
        assertEquals(initialScore + points, player.getScore());
        assertEquals(1, player.getPlayerDecisionsHistoric().size());
        assertEquals(1, player.getScoresHistoric().size());
        assertEquals(Decision.COOPERATE, player.getPlayerDecisionsHistoric().get(0));
        assertEquals(points, player.getScoresHistoric().get(0));
    }

    @Test
    void testDefaultConstructor() {
        // Act
        Player newPlayer = new Player("NewPlayer");

        // Assert
        assertNotNull(newPlayer.getPlayerId());
        assertEquals("NewPlayer", newPlayer.getName());
        assertTrue(newPlayer.isConnected());
        assertEquals(0, newPlayer.getScore());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        UUID newId = UUID.randomUUID();
        String newName = "UpdatedPlayer";
        Strategy newStrategy = mock(Strategy.class);

        // Act
        player.setPlayerId(newId);
        player.setName(newName);
        player.setStrategy(newStrategy);
        player.setConnected(false);

        // Assert
        assertEquals(newId, player.getPlayerId());
        assertEquals(newName, player.getName());
        assertEquals(newStrategy, player.getStrategy());
        assertFalse(player.isConnected());
    }
}