package fr.uga.l3miage.pc.prisonersdilemma.utilsTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testConstructorWithCodeMessageType() {
        ApiResponse<String> response = new ApiResponse<>(200, "OK", "displayResults");
        assertEquals(200, response.getCode(), "Le code doit être 200.");
        assertEquals("OK", response.getMessage(), "Le message doit être 'OK'.");
        assertEquals("displayResults", response.getType(), "Le type doit être 'displayResults'.");
        assertNull(response.getData(), "Les données doivent être nulles.");
    }

    //TODO resoudre ce test
    /*@Test
    void testConstructorWithAllParameters() {
        String data = "Test Data";
        ApiResponse<String> response = new ApiResponse<>(200, "OK", "displayResults", data);

        assertEquals(200, response.getCode(), "Le code doit être 200.");
        assertEquals("OK", response.getMessage(), "Le message doit être 'OK'.");
        assertEquals("displayResults", response.getType(), "Le type doit être 'displayResults'.");
        assertEquals(data, response.getData(), "Les données doivent être 'Test Data'.");
    }*/

    @Test
    void testSettersAndGetters() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(404);
        response.setMessage("Not Found");
        response.setType("error");
        response.setData("No data");

        assertEquals(404, response.getCode(), "Le code doit être 404.");
        assertEquals("Not Found", response.getMessage(), "Le message doit être 'Not Found'.");
        assertEquals("error", response.getType(), "Le type doit être 'error'.");
        assertEquals("No data", response.getData(), "Les données doivent être 'No data'.");
    }

    //TODO resoudre ce test
    /*@Test
    void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<String> response = new ApiResponse<>(200, "OK", "displayResults", "Serialized Data");

        String json = objectMapper.writeValueAsString(response);

        assertTrue(json.contains("\"code\":200"), "Le JSON doit contenir 'code: 200'.");
        assertTrue(json.contains("\"message\":\"OK\""), "Le JSON doit contenir 'message: OK'.");
        assertTrue(json.contains("\"type\":\"displayResults\""), "Le JSON doit contenir 'type: displayResults'.");
        assertTrue(json.contains("\"data\":\"Serialized Data\""), "Le JSON doit contenir 'data: Serialized Data'.");
    }*/

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"code\":200,\"message\":\"OK\",\"type\":\"displayResults\",\"data\":\"Deserialized Data\"}";

        ApiResponse<String> response = objectMapper.readValue(json, ApiResponse.class);

        assertEquals(200, response.getCode(), "Le code doit être 200.");
        assertEquals("OK", response.getMessage(), "Le message doit être 'OK'.");
        assertEquals("displayResults", response.getType(), "Le type doit être 'displayResults'.");
        assertEquals("Deserialized Data", response.getData(), "Les données doivent être 'Deserialized Data'.");
    }
}

