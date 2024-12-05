package fr.uga.l3miage.pc.prisonersdilemma.entities;

import fr.uga.l3miage.pc.prisonersdilemma.services.Round;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Decision;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoundHistoric {

    private List<Decision> player1DecisionsHistoric = new ArrayList<>();

    private List<Decision> player2DecisionsHistoric = new ArrayList<>();
}
