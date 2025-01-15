package fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases;


import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameServiceImpl;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.RoundServiceImpl;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.GameMapper;
import fr.uga.l3miage.pc.prisonersdilemma.severside.adaptation.FromGroup1_7ToGroup2_6StrategiesUsage;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.*;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.strategies.Pavlov;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.RoundReward;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.ScoringSystem;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.ApiResponse;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.services.GameService;
import fr.uga.l3miage.pc.prisonersdilemma.userside.dtos.GameDTO;
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

import static fr.uga.l3miage.pc.prisonersdilemma.businesslogic.utils.Type.*;

//Business Logic

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
    private RoundServiceImpl activeRound;
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
    
    public ApiResponse<GameDTO> joinGame(Player player2) {
        if (!this.availableToJoin) {
            return new ApiResponse<>(200, "Cette partie est déjà complète", joinGame);
        }
        this.thePlayer2 = player2;
        this.gameService = new GameServiceImpl();
        this.availableToJoin = false;
        //logger.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(this::start, 1, TimeUnit.MILLISECONDS); // Décaler de 500ms
        //logger.info("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        return new ApiResponse<>(200, "OK", joinGame, GameMapper.toGameDTO(this));
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

                activeRound = new RoundServiceImpl(countConnected);

                activeRound.waitForChoices();  // Attend que les deux joueurs aient fait leurs choix

                score = ScoringSystem.calculateScore(thePlayer1.getActualRoundDecision(), thePlayer2.getActualRoundDecision());

                thePlayer1.updateDatas(score.getPlayer1Reward());
                thePlayer2.updateDatas(score.getPlayer2Reward());

                if (thePlayer1.isConnected()) {
                    thePlayer1.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", displayResults, GameMapper.toGameDTO(this)));
                }

                if (thePlayer2.isConnected()) {
                    thePlayer2.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", displayResults, GameMapper.toGameDTO(this)));
                }

                this.resetPlayersDecisionForNextRound();
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
            thePlayer1.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", getResults, GameMapper.toGameDTO(this)));
            Thread.yield();//permet la synchronisation de la requête
        }

        if (thePlayer2.isConnected()) {
            thePlayer2.sendToPlayer(this.simpMessagingTemplate, new ApiResponse<>(200, "Roud " + (this.playedRound + 1) + " end successfuly", getResults, GameMapper.toGameDTO(this)));
            Thread.yield();//permet la synchronisation de la requête
        }

        cleanMySelfOfTheGlobalMap();
        //désallouer les ressources ?

    }

    public ApiResponse<GameDTO> playGame(UUID playerId, String decision) {
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
                return new ApiResponse<>(500, "Crash of synchronisation update process", playGame, GameMapper.toGameDTO(this));
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
                return new ApiResponse<>(500, "Crash of synchronisation update process", playGame, GameMapper.toGameDTO(this));
            }
        } else {
            return new ApiResponse<>(404, "The specified player hasn't been found", playGame, GameMapper.toGameDTO(this));
        }
        return new ApiResponse<>(200, "OK", playGame, GameMapper.toGameDTO(this));
    }

    public ApiResponse<GameDTO> giveUpGame(UUID playerId, String strategyName) {

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

            //ApiResponse<GameDTO> giveUpGame1 = standardVerificationAfterGivUp(playerId, strategyName);
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

    public ApiResponse<GameDTO> giveUpGameAdaptedToGroup1_7Strategies(UUID playerId, String strategyName) {

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

    private void resetPlayersDecisionForNextRound() {
        thePlayer1.setActualRoundDecision(null);
        thePlayer2.setActualRoundDecision(null);
    }

    private void cleanMySelfOfTheGlobalMap() {
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        gameMap.removeElement(this.gameId);
    }

    private Strategy initializeAutoStrategy(String strategyName, Player me, Player myOpponent) {

        return switch (strategyName) {
            case "AlwaysBetray" -> new AlwaysBetray();
            case "AlwaysCooperate" -> new AlwaysCooperate();
            case "RandomStrategy" -> new RandomStrategy();
            case "TitForTat" -> new TitForTat(myOpponent.getPlayerDecisionsHistoric());
            case "TitForTatRandom" -> new TitForTatRandom(myOpponent.getPlayerDecisionsHistoric());
            case "TitForTatTats" -> new TitForTwoTats(myOpponent.getPlayerDecisionsHistoric());
            case "TitForTatTatsRandom" -> new TitForTwoTatsRandom(myOpponent.getPlayerDecisionsHistoric());
            case "SuspiciousTitForTatStrategy" ->
                    new SuspiciousTitForTatStrategy(myOpponent.getPlayerDecisionsHistoric());
            case "TruePeacemaker" -> new TruePeacemaker(myOpponent.getPlayerDecisionsHistoric());
            case "RemorsefulProber" ->
                    new RemorsefulProber(me.getPlayerDecisionsHistoric(), myOpponent.getPlayerDecisionsHistoric());
            case "PavlovRandom" -> new PavlovRandom(me.getScoresHistoric(), me.getPlayerDecisionsHistoric());
            case "Pavlov" -> new Pavlov(me.getScoresHistoric(), me.getPlayerDecisionsHistoric());
            case "NaiveProber" -> new NaiveProber(myOpponent.getPlayerDecisionsHistoric());
            case "NaivePeacemaker" -> new NaivePeacemaker(myOpponent.getPlayerDecisionsHistoric());
            case "GimTrigger" -> new GrimTrigger(myOpponent.getPlayerDecisionsHistoric());
            case "GradualStrategy" -> new GradualStrategy(myOpponent.getPlayerDecisionsHistoric());
            case "ForgivingGrudgerStrategy" -> new ForgivingGrudgerStrategy(myOpponent.getPlayerDecisionsHistoric());
            case "Adaptive" -> new Adaptive(me.getScoresHistoric());
            default -> {
                logger.error("Type de Strategie inconnus");
                yield null;
            }
        };
    };

    private SimpleStrategy initializeAutoGroup1_7Strategy(String strategyName) {

        return switch (strategyName) {
            case "AlwaysBetray" -> new Trahir();
            case "AlwaysCooperate" -> new Cooperer();
            case "RandomStrategy" -> new Aleatoire();
            case "TitForTat" -> new DonnantDonnant();
            case "TitForTatRandom" -> new DonnantDonnantAleatoire();
            case "TitForTatTats" -> new DonnantPour2Donnants();
            case "TitForTatTatsRandom" -> new DonnantPour2DonnantsEtAleatoire();
            case "SuspiciousTitForTatStrategy" -> new DonnantDonnantSoupconneux();
            case "TruePeacemaker" -> new Pacificateur();
            case "RemorsefulProber" -> new SondeurRepentant();
            case "PavlovRandom" -> new PavlovAleatoire();
            case "Pavlov" -> new fr.uga.l3miage.pc.strategies.Pavlov();
            case "NaiveProber" -> new SondeurNaif();
            case "NaivePeacemaker" -> new PacificateurNaif();
            case "GimTrigger" -> new Rancunier();
            case "GradualStrategy" -> new Graduel();
            case "ForgivingGrudgerStrategy" -> new RancunierDoux();
            case "Adaptive" -> new Adaptatif();
            default -> {
                logger.error("Type de Strategie inconnus");
                yield null;
            }
        };
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

    public static Game findTheRightGame(UUID gameId) {
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        return gameMap.getElement(gameId);
    }

}
