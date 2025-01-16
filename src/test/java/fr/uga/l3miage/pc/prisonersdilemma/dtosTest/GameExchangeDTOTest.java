package fr.uga.l3miage.pc.prisonersdilemma.dtosTest;

import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameExchangeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameExchangeDTOTest {

    private GameExchangeDTO dto;

    @BeforeEach
    void setUp() {
        // Initialisation d'une instance de GameExchangeDTO avant chaque test
        dto = new GameExchangeDTO(5, UUID.randomUUID(), UUID.randomUUID(), "session123", "Player1", "COOPERATE");
    }

    @Test
    void testGetRounds() {
        // Act & Assert
        assertEquals(5, dto.getRounds());
    }

    @Test
    void testSetRounds() {
        // Arrange
        dto.setRounds(10);

        // Act & Assert
        assertEquals(10, dto.getRounds());
    }

    @Test
    void testGetGameId() {
        // Act & Assert
        assertNotNull(dto.getGameId());
    }

    @Test
    void testSetGameId() {
        // Arrange
        UUID newGameId = UUID.randomUUID();
        dto.setGameId(newGameId);

        // Act & Assert
        assertEquals(newGameId, dto.getGameId());
    }

    @Test
    void testGetPlayerId() {
        // Act & Assert
        assertNotNull(dto.getPlayerId());
    }

    @Test
    void testSetPlayerId() {
        // Arrange
        UUID newPlayerId = UUID.randomUUID();
        dto.setPlayerId(newPlayerId);

        // Act & Assert
        assertEquals(newPlayerId, dto.getPlayerId());
    }

    @Test
    void testGetPlayerSessionId() {
        // Act & Assert
        assertEquals("session123", dto.getPlayerSessionId());
    }

    @Test
    void testSetPlayerSessionId() {
        // Arrange
        dto.setPlayerSessionId("session456");

        // Act & Assert
        assertEquals("session456", dto.getPlayerSessionId());
    }

    @Test
    void testGetPlayerName() {
        // Act & Assert
        assertEquals("Player1", dto.getPlayerName());
    }

    @Test
    void testSetPlayerName() {
        // Arrange
        dto.setPlayerName("Player2");

        // Act & Assert
        assertEquals("Player2", dto.getPlayerName());
    }

    @Test
    void testGetPlayerDecision() {
        // Act & Assert
        assertEquals("COOPERATE", dto.getPlayerDecision());
    }

    @Test
    void testSetPlayerDecision() {
        // Arrange
        dto.setPlayerDecision("DEFECT");

        // Act & Assert
        assertEquals("DEFECT", dto.getPlayerDecision());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange
        GameExchangeDTO emptyDto = new GameExchangeDTO();

        // Act & Assert
        assertNotNull(emptyDto);
        assertEquals(0, emptyDto.getRounds());
        assertNull(emptyDto.getGameId());
        assertNull(emptyDto.getPlayerId());
        assertNull(emptyDto.getPlayerSessionId());
        assertNull(emptyDto.getPlayerName());
        assertNull(emptyDto.getPlayerDecision());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        GameExchangeDTO allArgsDto = new GameExchangeDTO(5, UUID.randomUUID(), UUID.randomUUID(), "session123", "Player1", "COOPERATE");

        // Assert
        assertEquals(5, allArgsDto.getRounds());
        assertNotNull(allArgsDto.getGameId());
        assertNotNull(allArgsDto.getPlayerId());
        assertEquals("session123", allArgsDto.getPlayerSessionId());
        assertEquals("Player1", allArgsDto.getPlayerName());
        assertEquals("COOPERATE", allArgsDto.getPlayerDecision());
    }
}