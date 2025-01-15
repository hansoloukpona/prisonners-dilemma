package fr.uga.l3miage.pc.prisonersdilemma.userside.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Decision;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Strategy;
import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data
public class PlayerDTO {

    private String name;
    private int score;
    private boolean isConnected;

    private UUID playerId;
    private Decision actualRoundDecision;
    private String playerSessionId;
}
