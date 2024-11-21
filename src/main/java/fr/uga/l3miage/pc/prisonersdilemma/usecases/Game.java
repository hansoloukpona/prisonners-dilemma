package fr.uga.l3miage.pc.prisonersdilemma.usecases;


import fr.uga.l3miage.pc.prisonersdilemma.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.services.GameService;
import fr.uga.l3miage.pc.prisonersdilemma.services.Round;
import fr.uga.l3miage.pc.prisonersdilemma.services.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.*;
import fr.uga.l3miage.pc.prisonersdilemma.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static fr.uga.l3miage.pc.prisonersdilemma.utils.Type.*;


public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private final int totalRounds;
    private GameService gameService;
    private int playedRound = 0;
    private boolean availableToJoin = true;
    private final UUID gameId;
    private Round activeRound;
    private final Player thePlayer1;
    private Player thePlayer2;

    public Game(int rounds, Player player1) {
        this.gameId = UUID.randomUUID();
        this.totalRounds = rounds;
        this.thePlayer1 = player1;
    }
    public ApiResponse<Game> joinGame(String player2Name, WebSocketSession player2Session) {
        if (!this.availableToJoin) {
            return new ApiResponse<>(200, "Cette partie est déjà complète", joinGame,null);
        }
        this.thePlayer2 = new Player(player2Name, playerSession);
        this.gameService = new GameService();
        this.availableToJoin = false;
        return new ApiResponse<>(200, "OK", joinGame, this);
    }

    @Async
    protected void start() {
        boolean finished = false;

        /*if (!this.gameService.playerIsPresentInTheGame(thePlayer2)) {
            // On verifie bien que le joueur 2 est connecté
        };*/

        for (playedRound = 1; playedRound <= totalRounds; playedRound++) {

            RoundReward score;
            try {
                activeRound = new Round();

                activeRound.waitForChoices();  // Attend que les deux joueurs aient fait leurs choix

                score = ScoringSystem.calculateScore(thePlayer1.getActualRoundDecision(), thePlayer2.getActualRoundDecision());

                thePlayer1.updateScore(score.getPlayer1Reward());
                thePlayer2.updateScore(score.getPlayer2Reward());

                //TODO Envoyer aux 2 tout simplement et on passe au tour suivant
                /*activeRound.waitForRoundResultConsultation();*/

                ApiResponse<Game> response = new ApiResponse<>(200, "Roud " + this.playedRound + 1 + " end successfuly", giveUpGame, this);
                thePlayer1.sendToPlayer(response);
                thePlayer2.sendToPlayer(response);

            } catch (InterruptedException e) {
                //TODO : gérer ceci en faisant un continue (saut) après avoir aretiré +1 a playedRound ou arreter la partie
                //e.printStackTrace();
                //logger.error(e.getMessage());
                logger.error("Crash of synchronisation process, Bye Bye");
                finished = true;
                break;
            }

            String messageForTheRound = "Round " + (playedRound) + ":\n" +
                    thePlayer1.getName() + " chose " + thePlayer1.getActualRoundDecision() + " and scored " + score.getPlayer1Reward() + " points.\n" +
                    thePlayer2.getName() + " chose " + thePlayer2.getActualRoundDecision() + " and scored " + score.getPlayer2Reward() + " points.\n";

            activeRound.setResulDataCompilation(messageForTheRound);

            //attendre que chacun aie lu le résultat : on fait un endpoint qu'on consulte toute les 3 secondes et qui donne les information par rapport au tour en cours comme round
        }
        if (!finished) {
            try {
                activeRound.initTheGameResultConsultation();
                activeRound.waitForTheGameResultConsultation();
            } catch (InterruptedException e) {
                //TODO
                //e.printStackTrace();
                //logger.error(e.getMessage());
                logger.error("Crash of result synchronisation process, Bye Bye");
                finished = true;
                //TODO sendMessage
            }
            if (!finished) {
                this.resetPlayersDecisionForNextRound();
                cleanMySelfOfTheGlobalMap();
                //désallouer les ressources ?
            }

        }

    }

    public ApiResponse<Game> playGame(UUID playerId, String decision) {
        try {
            GameService.decisionIsValid(decision);
        } catch (Exception e) {
            return new ApiResponse<>(404, "Specified decision not found", playGame, this);
        }

        if (!activeRound.isReadyForPlayersChoices())
            return new ApiResponse<>(503, "Round player choice listen Unavailable", playGame, this);

        if (gameService.verifyPlayer(playerId, thePlayer1)) {
            thePlayer1.setActualRoundDecision(Decision.valueOf(decision));
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                //TODO : gérer ceci en faisant un continue (saut) après avoir aretiré +1 a playedRound ou arreter la partie
                //e.printStackTrace();
                //logger.error(e.getMessage());
                logger.error("Crash of synchronisation update process, Bye Bye");
                return new ApiResponse<>(500, "Crash of synchronisation update process", playGame, this);
            }

        } else if (gameService.verifyPlayer(playerId, thePlayer2)) {
            thePlayer2.setActualRoundDecision(Decision.valueOf(decision));
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                //TODO : gérer ceci en faisant un continue (saut) après avoir retiré +1 a playedRound ou arreter la partie
                //e.printStackTrace();
                //logger.error(e.getMessage());
                logger.error("Crash of synchronisation update process, Bye Bye");
                return new ApiResponse<>(500, "Crash of synchronisation update process", playGame, this);
            }
        } else {
            return new ApiResponse<>(404, "The specified player hasn't been found", playGame, this);
        }
        return new ApiResponse<>(200, "OK", playGame, this);
    }

    public ApiResponse<Game> getGameState(UUID playerId) {
        try {
            gameService.userExistAndActiveInGame(playerId, thePlayer1, thePlayer2);
            if (!activeRound.isReadyForRoundResultConsultation()) {
                return new ApiResponse<>(503, "Round result still Unavailable", getGameState, this);
            }
            activeRound.countAPlayerChoice();
        } catch (Exception e) {
            return new ApiResponse<>(404, e.getMessage(), getGameState, this);
        }
        return new ApiResponse<>(200, activeRound.getResulDataCompilation(), getGameState, this);
    }

    public ApiResponse<Game> giveUpGame(UUID playerId, String strategyName) {

        //TODO trouver le joueur, et remplacer ses tours de passage par une strategie
        if(playerId == thePlayer2.getPlayerId()) {

            thePlayer2.setStrategy(initializeAutoStrategy(strategyName));
            //ApiResponse<Game> giveUpGame1 = standardVerificationAfterGivUp(playerId, strategyName);
        } else {
            thePlayer1.setStrategy(initializeAutoStrategy(strategyName));
        }

        //TODO vérifier si le joueur a déjà joué pour ce tout avant d'abandonner et agir en
        // conséquence selon le cas

        //TODO juste après avoir reçu la décision d'un joueur pour le tout actuel on véifie
        // que l'autre joeur est connecté ou a déjà donné sa décision, sinon on va déclencher le mouvement suivant la
        // strategie qu'il a choisi en partant
        return new ApiResponse<>(200, "OK", giveUpGame, this);
    }

    /*private ApiResponse<Game> standardVerificationAfterGivUp(UUID playerId, String strategyName) {
        if(playerId == thePlayer2.getPlayerId()) {

            thePlayer2.setStrategy(initializeAutoStrategy(strategyName));

            boolean strategyInitialised = isStrategyInitialised(thePlayer2.getStrategy());

            if (!strategyInitialised) {
                return new ApiResponse<>(500, "No strategy has been charged", giveUpGame, this);
            } else {
                return //TODO
            }
        } else {

            thePlayer1.setStrategy(initializeAutoStrategy(strategyName));
            boolean strategyInitialised = isStrategyInitialised(thePlayer2.getStrategy());

            if (!strategyInitialised) return new ApiResponse<>(500, "No strategy has been charged", giveUpGame, this);
        }
    }*/

    private boolean isStrategyInitialised( Strategy strategy) {
        if (strategy == null) {
            //TODO prévoir une fin de partie dans ce cas
            return false;
        }
        return true;
    }

    private void resetPlayersDecisionForNextRound() {
        thePlayer1.setActualRoundDecision(null);
        thePlayer2.setActualRoundDecision(null);
    }

    private void cleanMySelfOfTheGlobalMap() {
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        gameMap.removeElement(this.gameId);
    }

    public ApiResponse<String> displayResults() {

        if (!activeRound.isReadyForResultConsultation())
            return new ApiResponse<>(503, "The game final results aren't available yet", displayResults, "");

        //The # will serve to split the strig on the user client to display them one by one

        String resultsText;

        resultsText = "Game over!" + "\n" +
                thePlayer1.getName() + " final score: " + thePlayer1.getScore() + "\n" +
                thePlayer2.getName() + " final score: " + thePlayer2.getScore();

        if (thePlayer1.getScore() > thePlayer2.getScore()) {
            resultsText = resultsText + "\n" + thePlayer1.getName() + " wins!";
        } else if (thePlayer2.getScore() > thePlayer1.getScore()) {
            resultsText = resultsText + "\n" + thePlayer2.getName() + " wins!";
        } else {
            resultsText = resultsText + "\n" + "It's a tie!";
        }
        return new ApiResponse<>(200, "OK", displayResults, resultsText);
    }

    public UUID getGameId() {
        return gameId;
    }

    public boolean isAvailableToJoin() {
        return availableToJoin;
    }

    private Strategy initializeAutoStrategy(String strategyName) {

        switch (strategyName) {
            case "AlwaysBetray":
                return new AlwaysBetray();
            case "AlwaysCooperate":
                return new AlwaysCooperate();
            case "RandomStrategy":
                return new RandomStrategy();
            case "TitForTat":
                return new TitForTat();
            case "TitForTatRandom":
                return new TitForTatRandom();
            default:
                logger.error("Type de Strategie inconnus");
                return null;
        }
    };

    public void endGame() {
        logger.info(this.thePlayer1.getName() + " had end the party!, Bye Bye");
        displayResults();
        cleanMySelfOfTheGlobalMap();
        //désallouer les ressources ?
    }
}
