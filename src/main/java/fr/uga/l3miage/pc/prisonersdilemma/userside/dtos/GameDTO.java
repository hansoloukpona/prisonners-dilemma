package fr.uga.l3miage.pc.prisonersdilemma.userside.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameService;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.RoundService;
import fr.uga.l3miage.pc.prisonersdilemma.severside.adaptation.FromGroup1_7ToGroup2_6StrategiesUsage;
import lombok.Data;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

@Data
public class GameDTO {

    private int totalRounds;
    private int playedRound;
    private boolean availableToJoin;
    private UUID gameId;

    private boolean readyForPlayersChoices;

    private PlayerDTO thePlayer1;
    private PlayerDTO thePlayer2;

}
