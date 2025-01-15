package fr.uga.l3miage.pc.prisonersdilemma.utilsTests;

import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.entities.Player;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.Game;
import fr.uga.l3miage.pc.prisonersdilemma.businesslogic.usecases.GlobalGameMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalGameMapTest {

    @Test
    void testSingletonInstance() {
        // Vérifier que le singleton fonctionne et renvoie la même instance
        GlobalGameMap instance1 = GlobalGameMap.getInstance();
        GlobalGameMap instance2 = GlobalGameMap.getInstance();

        assertSame(instance1, instance2, "Les deux instances doivent être identiques (Singleton).");
    }

    @Test
    void testPutAndGetElement() {
        // Tester l'ajout et la récupération d'un élément
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        UUID gameId = UUID.randomUUID();
        Player player = new Player("hans");
        Game game = new Game(3, player); // Supposons que Game a un constructeur adapté
        game.setGameId(gameId);

        gameMap.putElement(gameId, game);
        Game retrievedGame = gameMap.getElement(gameId);

        assertNotNull(retrievedGame, "Le jeu doit être récupéré.");
        assertEquals(gameId, retrievedGame.getGameId(), "Le jeu récupéré doit avoir le même ID.");
    }

    @Test
    void testRemoveElement() {
        // Tester la suppression d'un élément
        GlobalGameMap gameMap = GlobalGameMap.getInstance();
        UUID gameId = UUID.randomUUID();
        Player player = new Player("leo");
        Game game = new Game(3, player);
        game.setGameId(gameId);

        gameMap.putElement(gameId, game);
        gameMap.removeElement(gameId);

        Game removedGame = gameMap.getElement(gameId);

        assertNull(removedGame, "Le jeu doit être supprimé de la carte.");
    }

    @Test
    void testGetGamesNotAvailableToJoin() {
        // Tester la logique de filtrage des jeux non disponibles à rejoindre
        GlobalGameMap gameMap = GlobalGameMap.getInstance();

        UUID gameId1 = UUID.randomUUID();
        Player player1 = new Player("jacques");
        Game game1 = new Game(3, player1); // Jeu non disponible
        gameMap.putElement(gameId1, game1);

        UUID gameId2 = UUID.randomUUID();
        Player player2 = new Player("kokou");
        Game game2 = new Game(3, player2); // Jeu disponible
        gameMap.putElement(gameId2, game2);

        List<UUID> gamesNotAvailable = gameMap.getGamesNotAvailableToJoin();

        assertEquals(3, gamesNotAvailable.size(), "Il doit y avoir 1 jeu non disponible.");
        assertTrue(gamesNotAvailable.contains(gameId1), "Le jeu non disponible doit être dans la liste.");
    }
}

