package fr.uga.l3miage.pc.prisonersdilemma.usecasesTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameServiceImpl;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.RoundServiceImpl;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Decision;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.Game;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.ApiResponse;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

class GameTest {

    @Mock
    private GameServiceImpl gameService;

    @Mock
    private RoundServiceImpl roundService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    private Game game;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        game = new Game(5, player1); // Initialiser une partie avec 5 rounds et player1
        game.setGameService(gameService);
        game.setSimpMessagingTemplate(simpMessagingTemplate);
    }

    @Test
    void testJoinGame() {
        // Arrange
        when(player1.isConnected()).thenReturn(true);
        when(player2.isConnected()).thenReturn(true);

        // Act
        ApiResponse response = game.joinGame(player2);

        // Assert
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        //verify(gameService, times(1)).start(any()); // Vérifie que la méthode startGame a bien été appelée
    }

    @Test
    void testStartGame() throws InterruptedException {
        // Arrange
        when(player1.isConnected()).thenReturn(true);
        when(player2.isConnected()).thenReturn(true);

        // Simuler la décision des joueurs
        when(player1.getActualRoundDecision()).thenReturn(Decision.COOPERATE);
        when(player2.getActualRoundDecision()).thenReturn(Decision.BETRAY);

        // Act
        /*game.start();

        // Assert
        verify(player1, times(1)).updateDatas(any()); // Vérifie que les scores ont été mis à jour pour player1
        verify(player2, times(1)).updateDatas(any()); // Vérifie que les scores ont été mis à jour pour player2
        verify(simpMessagingTemplate, times(2)).convertAndSend(any(), any()); // Vérifie que les messages ont bien été envoyés aux deux joueurs*/
    }

    /*@Test
    void testPlayGame() {
        // Arrange
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();
        when(player1.getPlayerId()).thenReturn(player1Id);
        when(player2.getPlayerId()).thenReturn(player2Id);

        // Act
        ApiResponse<GameDTO> response = game.playGame(player1Id, "COOPERATE");

        // Assert
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
    }

    @Test
    void testGiveUpGame() {
        // Arrange
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();
        when(player1.getPlayerId()).thenReturn(player1Id);
        when(player2.getPlayerId()).thenReturn(player2Id);

        // Simuler que player2 a donné sa décision et abandonne
        when(player2.isConnected()).thenReturn(false);

        // Act
        ApiResponse response = game.giveUpGame(player2Id, "AlwaysCooperate");

        // Assert
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        verify(player2, times(1)).setStrategy(any()); // Vérifie que la stratégie d'abandon a été correctement définie
    }*/

    @Test
    void testEndGame() {
        // Arrange
        when(player1.isConnected()).thenReturn(true);
        when(player2.isConnected()).thenReturn(true);

        // Act
        game.endGame();

        // Assert
        //verify(gameService, times(1)).endGame(any()); // Vérifie que la méthode endGame a été appelée
    }
}