package fr.uga.l3miage.pc.prisonersdilemma.userside.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

//User-Side

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class GameExchangeDTO {

    private int rounds;
    private UUID gameId;
    private UUID playerId;
    private String playerSessionId;
    private String playerName;
    private String playerDecision;

}
