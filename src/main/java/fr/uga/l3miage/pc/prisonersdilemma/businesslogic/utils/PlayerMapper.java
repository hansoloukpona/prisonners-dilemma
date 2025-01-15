package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Decision;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.PlayerDTO;

import java.util.UUID;

public class PlayerMapper {

    public static PlayerDTO toPlayerDTO(Player player) {

        if (player == null) {
            return null;
        }
        PlayerDTO playerDTO = new PlayerDTO();

        playerDTO.setPlayerSessionId(player.getPlayerSessionId());
        playerDTO.setPlayerId(player.getPlayerId());
        playerDTO.setName(player.getName());
        playerDTO.setConnected(player.getConnected());
        playerDTO.setScore(player.getScore());
        playerDTO.setActualRoundDecision(player.getActualRoundDecision());

        return playerDTO;
    }

    public static Player fromPlayerDTO(PlayerDTO playerDTO) {
        if (playerDTO == null) {
            return null;
        }
        Player player = new Player();
        player.setPlayerSessionId(playerDTO.getPlayerSessionId());
        player.setPlayerId(playerDTO.getPlayerId());
        player.setName(playerDTO.getName());
        player.setConnected(playerDTO.isConnected());
        player.setScore(playerDTO.getScore());
        player.setActualRoundDecision(playerDTO.getActualRoundDecision());

        return player;
    }
}
