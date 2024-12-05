package fr.uga.l3miage.pc.prisonersdilemma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class GameCreationDTO {

    private int rounds;
    private UUID gameId;
    private UUID playerId;
    private String playerSessionId;
    private String playerName;
    private String playerDecision;

}
