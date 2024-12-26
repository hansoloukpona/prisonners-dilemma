package fr.uga.l3miage.pc.prisonersdilemma.utils;


import fr.uga.l3miage.pc.prisonersdilemma.usecases.Game;

import java.util.*;
import java.util.stream.Collectors;

//Business Logic

public class GlobalGameMap {

    private static GlobalGameMap instance;
    private static Map<UUID, Game> map;

    private GlobalGameMap() {
        map = new HashMap<>();
    }

    public static GlobalGameMap getInstance() {
        if (instance == null) {
            instance = new GlobalGameMap();
        }
        return instance;
    }

    public Map<UUID, Game> getMap() {
        return map;
    }

    public void putElement(UUID key, Game oneGame) {
        map.put(key, oneGame);
    }

    public void removeElement(UUID key) {
        map.remove(key);
    }

    public Game getElement(UUID key) {
        return map.get(key);
    }

    public List<UUID> getGamesNotAvailableToJoin() {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().isAvailableToJoin())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}