package fr.uga.l3miage.pc.prisonersdilemma.entitiesTests;

import fr.uga.l3miage.pc.prisonersdilemma.entities.SimpleInformationExchange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleInformationExchangeTest {

    @Test
    void testConstructorAndGetters() {
        SimpleInformationExchange exchange = new SimpleInformationExchange("Alice", "Bob", "Hello!");

        assertEquals("Alice", exchange.getFrom());
        assertEquals("Bob", exchange.getTo());
        assertEquals("Hello!", exchange.getContent());
    }

    @Test
    void testSetters() {
        SimpleInformationExchange exchange = new SimpleInformationExchange("Alice", "Bob", "Hello!");

        exchange.setFrom("Charlie");
        exchange.setTo("Diana");
        exchange.setContent("Goodbye!");

        assertEquals("Charlie", exchange.getFrom());
        assertEquals("Diana", exchange.getTo());
        assertEquals("Goodbye!", exchange.getContent());
    }

    @Test
    void testEmptyContent() {
        SimpleInformationExchange exchange = new SimpleInformationExchange("Alice", "Bob", "");

        assertEquals("", exchange.getContent());
    }

    @Test
    void testNullValues() {
        SimpleInformationExchange exchange = new SimpleInformationExchange(null, null, null);

        assertNull(exchange.getFrom());
        assertNull(exchange.getTo());
        assertNull(exchange.getContent());
    }
}

