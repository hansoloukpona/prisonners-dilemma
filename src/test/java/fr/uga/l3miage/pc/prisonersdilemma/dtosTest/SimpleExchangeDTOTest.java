package fr.uga.l3miage.pc.prisonersdilemma.dtosTest;

import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.SimpleExchangeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleExchangeDTOTest {

    private SimpleExchangeDTO dto;

    @BeforeEach
    void setUp() {
        // Initialisation d'une instance de SimpleExchangeDTO avant chaque test
        dto = new SimpleExchangeDTO("Player1", "Player2", "Hello");
    }

    @Test
    void testGetFrom() {
        // Act & Assert
        assertEquals("Player1", dto.getFrom());
    }

    @Test
    void testSetFrom() {
        // Arrange
        dto.setFrom("Player3");

        // Act & Assert
        assertEquals("Player3", dto.getFrom());
    }

    @Test
    void testGetTo() {
        // Act & Assert
        assertEquals("Player2", dto.getTo());
    }

    @Test
    void testSetTo() {
        // Arrange
        dto.setTo("Player4");

        // Act & Assert
        assertEquals("Player4", dto.getTo());
    }

    @Test
    void testGetContent() {
        // Act & Assert
        assertEquals("Hello", dto.getContent());
    }

    @Test
    void testSetContent() {
        // Arrange
        dto.setContent("Goodbye");

        // Act & Assert
        assertEquals("Goodbye", dto.getContent());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange
        SimpleExchangeDTO emptyDto = new SimpleExchangeDTO();

        // Act & Assert
        assertNotNull(emptyDto);
        assertNull(emptyDto.getFrom());
        assertNull(emptyDto.getTo());
        assertNull(emptyDto.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        SimpleExchangeDTO allArgsDto = new SimpleExchangeDTO("Player1", "Player2", "Message");

        // Assert
        assertEquals("Player1", allArgsDto.getFrom());
        assertEquals("Player2", allArgsDto.getTo());
        assertEquals("Message", allArgsDto.getContent());
    }
}