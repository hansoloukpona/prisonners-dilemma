package fr.uga.l3miage.pc.prisonersdilemma.entitiesTests;

import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.SimpleExchangeDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleInformationExchangeTest {

    @Test
    void testConstructorAndGetters() {
        SimpleExchangeDTO exchange = new SimpleExchangeDTO("Alice", "Bob", "Hello!");

        assertEquals("Alice", exchange.getFrom());
        assertEquals("Bob", exchange.getTo());
        assertEquals("Hello!", exchange.getContent());
    }

    @Test
    void testSetters() {
        SimpleExchangeDTO exchange = new SimpleExchangeDTO("Alice", "Bob", "Hello!");

        exchange.setFrom("Charlie");
        exchange.setTo("Diana");
        exchange.setContent("Goodbye!");

        assertEquals("Charlie", exchange.getFrom());
        assertEquals("Diana", exchange.getTo());
        assertEquals("Goodbye!", exchange.getContent());
    }

    @Test
    void testEmptyContent() {
        SimpleExchangeDTO exchange = new SimpleExchangeDTO("Alice", "Bob", "");

        assertEquals("", exchange.getContent());
    }

    @Test
    void testNullValues() {
        SimpleExchangeDTO exchange = new SimpleExchangeDTO(null, null, null);

        assertNull(exchange.getFrom());
        assertNull(exchange.getTo());
        assertNull(exchange.getContent());
    }
}

