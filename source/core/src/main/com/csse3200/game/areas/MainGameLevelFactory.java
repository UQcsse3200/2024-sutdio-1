package com.csse3200.game.areas;

import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.entities.Room;
import com.csse3200.game.entities.factories.CollectibleFactory;
import com.csse3200.game.entities.factories.NPCFactory;
import com.csse3200.game.entities.factories.RoomFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the main game mode.
 */
public class MainGameLevelFactory implements LevelFactory {
    private static final int DEFAULT_MAP_SIZE = 40;

    @Override
    public Level create(int levelNumber) {
        LevelMap map = new LevelMap("seed", DEFAULT_MAP_SIZE);
        RoomFactory roomFactory = new RoomFactory(
                new NPCFactory(),
                new CollectibleFactory(),
                new TerrainFactory()
        );
        // Sprint 4 Switch the MapGenerator to use Rooms
        Map<String, Room> rooms = new HashMap<>();

        Set<String> room_keySet = map.mapData.getPositions().keySet();
        
        for (String room_key : room_keySet) {
            rooms.put(room_key, roomFactory.createRoom(
              map.mapData.getPositions().get(room_key),
             "0,0,14,10,0,0"));

            if (rooms.size() == 8) {
                rooms.put(room_key, roomFactory.createBossRoom(
                        map.mapData.getPositions().get(room_key),
                        "0,0,14,10," + levelNumber + levelNumber));
            }

        }
        //rooms.put("0_1", roomFactory.createBossRoom("0,0,14,10,0,0"));
        return new Level(map, levelNumber, rooms);
    }
}