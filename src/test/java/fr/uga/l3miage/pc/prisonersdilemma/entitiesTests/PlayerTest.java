package fr.uga.l3miage.pc.prisonersdilemma.entitiesTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Decision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Player player;
    private Strategy mockStrategy;
    private SimpMessagingTemplate mockSimpMessagingTemplate;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(Strategy.class);
        mockSimpMessagingTemplate = mock(SimpMessagingTemplate.class);
        player = new Player("TestPlayer");
    }

    @Test
    void constructorInitializesFieldsCorrectly() {
        assertEquals("TestPlayer", player.getName());
        assertTrue(player.isConnected());
        assertNotNull(player.getPlayerId());
    }

    @Test
    void playReturnsStrategyDecisionWhenNotConnected() {
        player.setConnected(false);
        player.setStrategy(mockStrategy);

        when(mockStrategy.nextMove()).thenReturn(Decision.COOPERATE);

        Decision decision = player.play();

        assertEquals(Decision.COOPERATE, decision);
        verify(mockStrategy, times(1)).nextMove();
    }

    //TODO
    /*@Test
    void playDoesNothingWhenConnected() {
        player.setConnected(true);
        player.setStrategy(mockStrategy);

        Decision decision = player.play();

        assertNull(decision);
        verify(mockStrategy, never()).nextMove();
    }*/

    @Test
    void sendToPlayerSendsMessageIfConnected() {
        // Arrange
        player.setConnected(true);
        player.setPlayerSessionId("session123");
        String message = "Test Message";

        // Act
        player.sendToPlayer(mockSimpMessagingTemplate, message);

        // Assert
        verify(mockSimpMessagingTemplate).convertAndSend(
                "/dilemma-game/clients/private/direct-usersession123",
                message
        );
    }


    @Test
    void sendToPlayerDoesNothingIfNotConnected() {
        player.setConnected(false);

        player.sendToPlayer(mockSimpMessagingTemplate, "Test Message");

        verify(mockSimpMessagingTemplate, never())
                .convertAndSendToUser(anyString(), any(), any());
    }

    @Test
    void giveUpSetsStrategyAndDisconnects() {
        Strategy newStrategy = mock(Strategy.class);

        player.giveUp(newStrategy);

        assertFalse(player.isConnected());
        assertEquals(newStrategy, player.getStrategy());
    }

    @Test
    void updateScoreAddsPointsToCurrentScore() {
        player.updateDatas(10);
        assertEquals(10, player.getScore());

        player.updateDatas(5);
        assertEquals(15, player.getScore());
    }
}

