package fr.uga.l3miage.pc.prisonersdilemma.usecases;


import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uga.l3miage.pc.prisonersdilemma.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.services.GameService;
import fr.uga.l3miage.pc.prisonersdilemma.services.Round;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.*;
import fr.uga.l3miage.pc.prisonersdilemma.utils.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static fr.uga.l3miage.pc.prisonersdilemma.utils.Type.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private int totalRounds;

    @JsonIgnore
    private GameService gameService;

    private int playedRound = 0;
    private boolean availableToJoin = true;
    private UUID gameId;
    private Round activeRound;
    private Player thePlayer1;
    private Player thePlayer2;

    @JsonIgnore
    private SimpMessagingTemplate simpMessagingTemplate;

    public Game(int rounds, Player player1) {
        this.gameId = UUID.randomUUID();
        this.totalRounds = rounds;
        this.thePlayer1 = player1;
    }
    public ApiResponse<Game> joinGame(Player player2) {
        if (!this.availableToJoin) {
            return new ApiResponse<>(200, "Cette partie est déjà complète", joinGame);
        }
        this.thePlayer2 = player2;
        this.gameService = new GameService();
        this.availableToJoin = false;
        //logger.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::start, 50, TimeUnit.MILLISECONDS); // Décaler de 500ms
        //logger.info("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        return new ApiResponse<>(200, "OK", joinGame, this);
    }

    protected void start() {
        //boolean finished = false;

        /*if (!this.gameService.playerIsPresentInTheGame(thePlayer2)) {
            // On verifie bien que le joueur 2 est connecté
        };*/

        for (playedRound = 1; playedRound <= totalRounds; playedRound++) {

            RoundReward score;
            try {

                int countConnected = 2;

                if (!thePlayer1.isConnected()) {
                    thePlayer1.play();
                    countConnected--;
                }

                if (!thePlayer2.isConnected()) {
                    thePlayer2.play();
                    countConnected--;
                }

                if (countConnected == 0) {
                    //TODO effacer cette vérification
                    logger.error("Ce dode n'est pas censé pouvoir se délencher");
                    break;
                }

                activeRound = new Round(countConnected);

                activeRound.waitForChoices();  // Attend que les deux joueurs aient fait leurs choix

                score = ScoringSystem.calculateScore(thePlayer1.getActualRoundDecision(), thePlayer2.getActualRoundDecision());

                thePlayer1.updateScore(score.getPlayer1Reward());
                thePlayer2.updateScore(score.getPlayer2Reward());

                //TODO Envoyer aux 2 tout simplement et on passe au tour suivant
                /*activeRound.waitForRoundResultConsultation();*/

                thePlayer1.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + this.playedRound + 1 + " end successfuly", displayResults, this));
                thePlayer2.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + this.playedRound + 1 + " end successfuly", displayResults, this));
                //this.resetPlayersDecisionForNextRound();
            } catch (InterruptedException e) {
                //TODO : gérer ceci en faisant un continue (saut) après avoir aretiré +1 a playedRound ou arreter la partie
                logger.error(e.getMessage());
                logger.error("Crash of synchronisation process, Bye Bye");
                break;
            }

            String messageForTheRound = "Round " + (playedRound) + ":\n" +
                    thePlayer1.getName() + " chose " + thePlayer1.getActualRoundDecision() + " and scored " + score.getPlayer1Reward() + " points.\n" +
                    thePlayer2.getName() + " chose " + thePlayer2.getActualRoundDecision() + " and scored " + score.getPlayer2Reward() + " points.\n";

            logger.info(messageForTheRound);

        }

        thePlayer1.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + this.playedRound + 1 + " end successfuly", getResults, this));
        thePlayer2.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + this.playedRound + 1 + " end successfuly", getResults, this));

        cleanMySelfOfTheGlobalMap();
        //désallouer les ressources ?

    }

    public ApiResponse<Game> playGame(UUID playerId, String decision) {
        if (!GameService.decisionIsValid(decision)) {
            decision = Decision.COOPERATE.toString();
            //TODO : Ajouter dans la doc que si tu met n'importe quoi, tu Coopère
            //return new ApiResponse<>(404, "Specified decision not found", playGame, this);
        }

        if (!activeRound.isReadyForPlayersChoices()) {
            //TODO Ajouter la gestion du fait que après avoir joué deux fois on attends de passer au round suivant
            //TODO En gros on fait un retour au client et on lui dit de rejouer pour ce tour ? ou on stocke et on réserve pour le move suivant ?
            logger.error("Tout ça va trop vite");
            //return new ApiResponse<>(500, "Round player choice listen Unavailable", playGame, this);
        }

        if ( playerId.toString().equals(thePlayer1.getPlayerId().toString())       /*gameService.verifyPlayer(playerId, thePlayer1)*/) {
            thePlayer1.setActualRoundDecision(Decision.valueOf(decision));
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                //TODO : gérer ceci en faisant un continue (saut) après avoir retiré +1 a playedRound ou arreter la partie
                //e.printStackTrace();
                logger.error(e.getMessage());
                logger.error("Crash of synchronisation update process, Bye Bye");
                return new ApiResponse<>(500, "Crash of synchronisation update process", playGame, this);
            }

        } else if ( playerId.toString().equals(thePlayer2.getPlayerId().toString())) {
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

    /*public ApiResponse<Game> getGameState(UUID playerId) {
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
    }*/

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

    /*public ApiResponse<String> displayResults() {

        if (!activeRound.isReadyForResultConsultation())
            return new ApiResponse<>(503, "The game final results aren't available yet", displayResults);

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
    }*/

    private Strategy initializeAutoStrategy(String strategyName) {

        switch (strategyName) {
            case "AlwaysBetray":
                return new AlwaysBetray();
            case "AlwaysCooperate":
                return new AlwaysCooperate();
            case "RandomStrategy":
                return new RandomStrategy();
            /*case "TitForTat":
                return new TitForTat();
            case "TitForTatRandom":
                return new TitForTatRandom();*/
            default:
                logger.error("Type de Strategie inconnus");
                return null;
        }
    };

    public void endGame() {
        //Vérifier que c'est bien le player 1
        logger.info(this.thePlayer1.getName() + " had end the party!, Bye Bye");
        //displayResults();
        //TODO send game to the 2 players
        cleanMySelfOfTheGlobalMap();
        //désallouer les ressources ?
    }
/*
    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public GameService getGameService() {
        return gameService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public int getPlayedRound() {
        return playedRound;
    }

    public void setPlayedRound(int playedRound) {
        this.playedRound = playedRound;
    }

    public boolean isAvailableToJoin() {
        return availableToJoin;
    }

    public void setAvailableToJoin(boolean availableToJoin) {
        this.availableToJoin = availableToJoin;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public Round getActiveRound() {
        return activeRound;
    }

    public void setActiveRound(Round activeRound) {
        this.activeRound = activeRound;
    }

    public Player getThePlayer1() {
        return thePlayer1;
    }

    public void setThePlayer1(Player thePlayer1) {
        this.thePlayer1 = thePlayer1;
    }

    public Player getThePlayer2() {
        return thePlayer2;
    }

    public void setThePlayer2(Player thePlayer2) {
        this.thePlayer2 = thePlayer2;
    }*/
}
