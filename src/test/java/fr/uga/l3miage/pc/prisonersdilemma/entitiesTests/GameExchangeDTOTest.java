package fr.uga.l3miage.pc.prisonersdilemma.entitiesTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameExchangeDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameExchangeDTOTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Créez un objet GameExchangeDTO
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        GameExchangeDTO dto = new GameExchangeDTO(5, gameId, playerId, "session123", "Player1", "COOPERATE");

        // Sérialisez en JSON
        String json = objectMapper.writeValueAsString(dto);

        // Désérialisez le JSON
        GameExchangeDTO deserializedDto = objectMapper.readValue(json, GameExchangeDTO.class);

        // Vérifiez que les valeurs restent identiques après désérialisation
        assertEquals(dto.getRounds(), deserializedDto.getRounds());
        assertEquals(dto.getGameId(), deserializedDto.getGameId());
        assertEquals(dto.getPlayerId(), deserializedDto.getPlayerId());
        assertEquals(dto.getPlayerSessionId(), deserializedDto.getPlayerSessionId());
        assertEquals(dto.getPlayerName(), deserializedDto.getPlayerName());
        assertEquals(dto.getPlayerDecision(), deserializedDto.getPlayerDecision());
    }
}

