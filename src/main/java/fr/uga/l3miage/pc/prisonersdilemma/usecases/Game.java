package fr.uga.l3miage.pc.prisonersdilemma.usecases;


import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uga.l3miage.pc.prisonersdilemma.adaptation.FromGroup1_7ToGroup2_6StrategiesUsage;
import fr.uga.l3miage.pc.prisonersdilemma.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.services.GameService;
import fr.uga.l3miage.pc.prisonersdilemma.services.Round;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Pavlov;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.Strategy;
import fr.uga.l3miage.pc.prisonersdilemma.services.strategies.*;
import fr.uga.l3miage.pc.prisonersdilemma.utils.*;
import fr.uga.l3miage.pc.strategies.*;
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

    @JsonIgnore
    private static FromGroup1_7ToGroup2_6StrategiesUsage adapter;

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
                    //thePlayer1.setActualRoundDecision(adapter.play(thePlayer1, thePlayer2, playedRound, thePlayer1.getPlayerId().toString())); //Group1_7 adaptation
                    countConnected--;
                }

                if (!thePlayer2.isConnected()) {
                    thePlayer2.play();
                    //thePlayer2.setActualRoundDecision(adapter.play(thePlayer1, thePlayer2, playedRound, thePlayer2.getPlayerId().toString()));
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

                thePlayer1.updateDatas(score.getPlayer1Reward());
                thePlayer2.updateDatas(score.getPlayer2Reward());

                //TODO Envoyer aux 2 tout simplement et on passe au tour suivant
                /*activeRound.waitForRoundResultConsultation();*/

                if (thePlayer1.isConnected()) {
                    thePlayer1.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", displayResults, this));
                }

                if (thePlayer2.isConnected()) {
                    thePlayer2.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", displayResults, this));
                }

                //this.resetPlayersDecisionForNextRound();
            } catch (InterruptedException e) {
                //TODO : gérer ceci en faisant un continue (saut) après avoir a retiré +1 a playedRound ou arreter la partie
                logger.error(e.getMessage());
                logger.error("Crash of synchronisation process, Bye Bye");
                break;
            }

            /*String messageForTheRound = "Round " + (playedRound) + ":\n" +
                    thePlayer1.getName() + " chose " + thePlayer1.getActualRoundDecision() + " and scored " + score.getPlayer1Reward() + " points.\n" +
                    thePlayer2.getName() + " chose " + thePlayer2.getActualRoundDecision() + " and scored " + score.getPlayer2Reward() + " points.\n";

            logger.info(messageForTheRound);*/

        }

        if (thePlayer1.isConnected()) {
            thePlayer1.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", getResults, this));
            Thread.yield();//permet la synchronisation de la requête
        }

        if (thePlayer2.isConnected()) {
            thePlayer2.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", getResults, this));
            Thread.yield();//permet la synchronisation de la requête
        }

        cleanMySelfOfTheGlobalMap();
        //désallouer les ressources ?

    }

    public ApiResponse<Game> playGame(UUID playerId, String decision) {
        /*if (!GameService.decisionIsValid(decision)) {
            decision = Decision.COOPERATE.toString();
            //TODO : Ajouter dans la doc que si tu met n'importe quoi, tu Coopère
            //return new ApiResponse<>(404, "Specified decision not found", playGame, this);
        }*/

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
                logger.info("le player 1 vient de jouer là");
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
                logger.info("le player 2 vient de jouer là");
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
        if(playerId.toString().equals(thePlayer2.getPlayerId().toString())) {

            if (!thePlayer1.isConnected()) {
                endGame();
            }

            thePlayer2.setStrategy(initializeAutoStrategy(strategyName, thePlayer2, thePlayer1));
            thePlayer2.setConnected(false);
            thePlayer2.play();
            //logger.info("On joue 2 de manière autoooooooooooooooooooooooooooooooooooooooooooooooooooo ");
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //ApiResponse<Game> giveUpGame1 = standardVerificationAfterGivUp(playerId, strategyName);
        } else if (playerId.toString().equals(thePlayer1.getPlayerId().toString())) {

            if (!thePlayer2.isConnected()) {
                endGame();
            }

            thePlayer1.setStrategy(initializeAutoStrategy(strategyName, thePlayer1, thePlayer2));
            thePlayer1.setConnected(false);
            thePlayer1.play();
            //logger.info("On joue 1 de manière autoooooooooooooooooooooooooooooooooooooooooooooooooooo ");
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //TODO vérifier si le joueur a déjà joué pour ce tout avant d'abandonner et agir en
        // conséquence selon le cas

        //TODO juste après avoir reçu la décision d'un joueur pour le tout actuel on véifie
        // que l'autre joeur est connecté ou a déjà donné sa décision, sinon on va déclencher le mouvement suivant la
        // strategie qu'il a choisi en partant
        return new ApiResponse<>(200, "OK", giveUpGame);
    }

    public ApiResponse<Game> giveUpGameAdaptedToGroup1_7Strategies(UUID playerId, String strategyName) {

        //TODO trouver le joueur, et remplacer ses tours de passage par une strategie
        if(playerId.toString().equals(thePlayer2.getPlayerId().toString())) {

            if (!thePlayer1.isConnected()) {
                endGame();
            }

            adapter = new FromGroup1_7ToGroup2_6StrategiesUsage(initializeAutoGroup1_7Strategy(strategyName),thePlayer2, thePlayer1, totalRounds);
            thePlayer2.setConnected(false);
            thePlayer2.play();
            //logger.info("On joue 2 de manière autoooooooooooooooooooooooooooooooooooooooooooooooooooo ");
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else if (playerId.toString().equals(thePlayer1.getPlayerId().toString())) {

            if (!thePlayer2.isConnected()) {
                endGame();
            }

            adapter = new FromGroup1_7ToGroup2_6StrategiesUsage(initializeAutoGroup1_7Strategy(strategyName),thePlayer2, thePlayer1, totalRounds);
            thePlayer1.setConnected(false);
            thePlayer1.play();
            //logger.info("On joue 1 de manière autoooooooooooooooooooooooooooooooooooooooooooooooooooo ");
            try {
                activeRound.countAPlayerChoice();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //TODO vérifier si le joueur a déjà joué pour ce tout avant d'abandonner et agir en
        // conséquence selon le cas

        //TODO juste après avoir reçu la décision d'un joueur pour le tout actuel on véifie
        // que l'autre joeur est connecté ou a déjà donné sa décision, sinon on va déclencher le mouvement suivant la
        // strategie qu'il a choisi en partant
        return new ApiResponse<>(200, "OK", giveUpGame);
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

    private Strategy initializeAutoStrategy(String strategyName, Player me, Player myOpponent) {

        switch (strategyName) {
            case "AlwaysBetray":
                return new AlwaysBetray();
            case "AlwaysCooperate":
                return new AlwaysCooperate();
            case "RandomStrategy":
                return new RandomStrategy();
            case "TitForTat":
                return new TitForTat(myOpponent.getPlayerDecisionsHistoric());
            case "TitForTatRandom":
                return new TitForTatRandom(myOpponent.getPlayerDecisionsHistoric());
            case "TitForTatTats":
                return new TitForTwoTats(myOpponent.getPlayerDecisionsHistoric());
            case "TitForTatTatsRandom":
                return new TitForTwoTatsRandom(myOpponent.getPlayerDecisionsHistoric());
            case "SuspiciousTitForTatStrategy":
                return new SuspiciousTitForTatStrategy(myOpponent.getPlayerDecisionsHistoric());
            case "TruePeacemaker":
                return new TruePeacemaker(myOpponent.getPlayerDecisionsHistoric());
            case "RemorsefulProber":
                return new RemorsefulProber(me.getPlayerDecisionsHistoric(), myOpponent.getPlayerDecisionsHistoric());
            case "PavlovRandom":
                return new PavlovRandom(me.getScoresHistoric(), me.getPlayerDecisionsHistoric());
            case "Pavlov":
                return new Pavlov(me.getScoresHistoric(), me.getPlayerDecisionsHistoric());
            case "NaiveProber":
                return new NaiveProber(myOpponent.getPlayerDecisionsHistoric());
            case "NaivePeacemaker":
                return new NaivePeacemaker(myOpponent.getPlayerDecisionsHistoric());
            case "GimTrigger":
                return new GrimTrigger(myOpponent.getPlayerDecisionsHistoric());
            case "GradualStrategy":
                return new GradualStrategy(myOpponent.getPlayerDecisionsHistoric());
            case "ForgivingGrudgerStrategy":
                return new ForgivingGrudgerStrategy(myOpponent.getPlayerDecisionsHistoric());
            case "Adaptive":
                return new Adaptive(me.getScoresHistoric());
            default:
                logger.error("Type de Strategie inconnus");
                return null;
        }
    };

    private SimpleStrategy initializeAutoGroup1_7Strategy(String strategyName) {

        switch (strategyName) {
            case "AlwaysBetray":
                return new Trahir();
            case "AlwaysCooperate":
                return new Cooperer();
            case "RandomStrategy":
                return new Aleatoire();
            case "TitForTat":
                return new DonnantDonnant();
            case "TitForTatRandom":
                return new DonnantDonnantAleatoire();
            case "TitForTatTats":
                return new DonnantPour2Donnants();
            case "TitForTatTatsRandom":
                return new DonnantPour2DonnantsEtAleatoire();
            case "SuspiciousTitForTatStrategy":
                return new DonnantDonnantSoupconneux();
            case "TruePeacemaker":
                return new Pacificateur();
            case "RemorsefulProber":
                return new SondeurRepentant();
            case "PavlovRandom":
                return new PavlovAleatoire();
            case "Pavlov":
                return new fr.uga.l3miage.pc.strategies.Pavlov();
            case "NaiveProber":
                return new SondeurNaif();
            case "NaivePeacemaker":
                return new PacificateurNaif();
            case "GimTrigger":
                return new Rancunier();
            case "GradualStrategy":
                return new Graduel();
            case "ForgivingGrudgerStrategy":
                return new RancunierDoux();
            case "Adaptive":
                return new Adaptatif();
            default:
                logger.error("Type de Strategie inconnus");
                return null;
        }
    }

    public void endGame() {
        //Vérifier que c'est bien le player 1
        logger.info(this.thePlayer1.getName() + " had end the party!, Bye Bye");

        this.gameService = null;
        this.thePlayer1 = null;
        this.thePlayer2 = null;
        this.activeRound = null;
        this.simpMessagingTemplate = null;

        //displayResults();
        //TODO faire disparaître cette classe et stopper la fonction play()
        cleanMySelfOfTheGlobalMap();
        //désallouer les ressources ?

    }

}
