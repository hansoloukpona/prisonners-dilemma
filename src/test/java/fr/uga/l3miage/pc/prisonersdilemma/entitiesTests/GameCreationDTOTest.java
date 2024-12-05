package fr.uga.l3miage.pc.prisonersdilemma.entitiesTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uga.l3miage.pc.prisonersdilemma.entities.GameCreationDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameCreationDTOTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Créez un objet GameCreationDTO
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        GameCreationDTO dto = new GameCreationDTO(5, gameId, playerId, "session123", "Player1", "COOPERATE");

        // Sérialisez en JSON
        String json = objectMapper.writeValueAsString(dto);

        // Désérialisez le JSON
        GameCreationDTO deserializedDto = objectMapper.readValue(json, GameCreationDTO.class);

        // Vérifiez que les valeurs restent identiques après désérialisation
        assertEquals(dto.getRounds(), deserializedDto.getRounds());
        assertEquals(dto.getGameId(), deserializedDto.getGameId());
        assertEquals(dto.getPlayerId(), deserializedDto.getPlayerId());
        assertEquals(dto.getPlayerSessionId(), deserializedDto.getPlayerSessionId());
        assertEquals(dto.getPlayerName(), deserializedDto.getPlayerName());
        assertEquals(dto.getPlayerDecision(), deserializedDto.getPlayerDecision());
    }
}

