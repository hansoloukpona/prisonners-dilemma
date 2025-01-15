package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;

public interface GameService {

    boolean playerIsPresentInTheGame(Player thePlayer);

    boolean decisionIsValid(String decision);
}
