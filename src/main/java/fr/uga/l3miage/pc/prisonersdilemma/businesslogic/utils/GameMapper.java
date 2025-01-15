package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.RoundServiceImpl;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.Game;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GameMapper {

    public static GameDTO toGameDTO (Game game) {

        if (game == null) {
            return null;
        }
        GameDTO dto = new GameDTO();

        dto.setGameId(game.getGameId());
        dto.setTotalRounds(game.getTotalRounds());
        dto.setPlayedRound(game.getPlayedRound());
        dto.setAvailableToJoin(game.isAvailableToJoin());

        if (game.getActiveRound() != null) {
            dto.setReadyForPlayersChoices(game.getActiveRound().isReadyForPlayersChoices());
        }

        dto.setThePlayer1(PlayerMapper.toPlayerDTO(game.getThePlayer1()));
        dto.setThePlayer2(PlayerMapper.toPlayerDTO(game.getThePlayer2()));

        return dto;
    }

    public static Game fromGameDTO (GameDTO dto) {
        if (dto == null) {
            return null;
        }
        Game game = new Game();
        game.setGameId(dto.getGameId());
        game.setTotalRounds(dto.getTotalRounds());
        game.setPlayedRound(dto.getPlayedRound());
        game.setAvailableToJoin(dto.isAvailableToJoin());

        if (game.getActiveRound() == null) {
            game.setActiveRound(new RoundServiceImpl(0));
        }
        Objects.requireNonNull(game.getActiveRound()).setReadyForPlayersChoices(dto.isReadyForPlayersChoices());

        game.setThePlayer1(PlayerMapper.fromPlayerDTO(dto.getThePlayer1()));
        game.setThePlayer2(PlayerMapper.fromPlayerDTO(dto.getThePlayer2()));

        return game;
    }
}
