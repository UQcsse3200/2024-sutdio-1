package com.csse3200.game.areas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.minimap.MinimapComponent;
import com.csse3200.game.areas.minimap.MinimapFactory;
import com.csse3200.game.components.player.inventory.InventoryComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.AlertBoxService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.entities.configs.MapLoadConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Controller for complex methods related to rooms and map code.
 */
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public static final String MAP_SAVE_PATH = "saves/MapSave.json";

    /**
     * The main player entity.
     */
    private final Entity player;

    /**
     * Factory for creating game levels.
     */
    private final LevelFactory levelFactory;

    /**
     * Flag to determine if the game should load a saved state.
     */
    private final boolean shouldLoad;

    /**
     * Map to store the current position of the player.
     */
    private final Map<String, String> currentPosition = new HashMap<>();

    /**
     * Name of the current room.
     */
    public String currentRoomName;

    /**
     * Current level of the game.
     */
    private Level currentLevel;

    /**
     * Current level number.
     */
    private int currentLevelNumber;

    /**
     * Current room the player is in.
     */
    private Room currentRoom;

    /**
     * Flag to determine if a new room should be spawned.
     */
    private boolean spawnRoom = true;

    /**
     * Factory for creating minimaps.
     */
    private MinimapFactory minimapFactory;

    private GameArea gameArea;

    private MapLoadConfig config;


    /**
     * Initialise this Game Area to use the provided levelFactory.
     *
     * @param levelFactory the provided levelFactory.
     * @param player       the main player entity.
     * @param shouldLoad   flag to determine if a saved state should be loaded.
     * @param config       the configuration for map loading.
     */
    public GameController(GameArea gameArea, LevelFactory levelFactory, Entity player, boolean shouldLoad, MapLoadConfig config) {
        if (config != null) {
            this.config = config;
        }
        this.player = player;
        this.levelFactory = levelFactory;
        this.shouldLoad = shouldLoad;
        this.gameArea = gameArea;
        player.getEvents().addListener("teleportToBoss", () -> this.changeRooms(getFlaggedRoom("Boss")));
        player.getEvents().addListener("teleportToShop", () -> this.changeRooms(getFlaggedRoom("Shop")));
        player.getEvents().addListener("saveMapData", this::saveMapData);
        player.getEvents().addListener("checkAnimalsDead", () -> this.getCurrentRoom().checkComplete());
        ServiceLocator.registerGameAreaService(new GameAreaService(this));
        create();
    }

    /**
     * Initialise this Game Area to use the provided levelFactory without loading a saved state.
     *
     * @param levelFactory the provided levelFactory.
     * @param player       the main player entity.
     */
    public GameController(GameArea gameArea, LevelFactory levelFactory, Entity player) {
        this(gameArea, levelFactory, player, false, new MapLoadConfig());
    }

    /**
     * Initialise this Game Area to use the provided levelFactory without loading a saved state.
     *
     * @param levelFactory the provided levelFactory.
     * @param shouldLoad   if the game should be loaded.
     * @param player       the main player entity.
     */
    public GameController(GameArea gameArea, LevelFactory levelFactory, Entity player, Boolean shouldLoad) {
        this(gameArea, levelFactory, player, false, new MapLoadConfig());
    }

    /**
     * Create the game area, including terrain, static entities (trees), and dynamic entities (player).
     */
    public void create() {
        this.gameArea.displayUI("BEAST BREAKOUT FACILITY");
        getGameArea().load();
        logger.error("loaded all assets");




        if (shouldLoad) {
            changeLevel(Integer.parseInt(config.currentLevel));
            updateLevel();
            changeRooms(config.currentRoom);
        } else {
            changeLevel(0);
            updateLevel();
        }
        getGameArea().playMusic(0);
    }

    /**
     * Get the room key for a specified flagged room type.
     *
     * @param roomType The type of room to get the key for. Valid values are "Boss" and "Shop".
     * @return The room key for the specified room type, or null if the room type is invalid.
     * @throws IllegalArgumentException if an invalid room type is provided.
     */
    public String getFlaggedRoom(String roomType) {
        if (!List.of("Boss", "Shop").contains(roomType)) {
            throw new IllegalArgumentException("Invalid room type: " + roomType);
        }
        return currentLevel.getMap().getMapData().getRoomKeys().get(roomType);
    }

    /**
     * Get the main player of this game area.
     *
     * @return the player entity.
     */
    public Entity getPlayer() {
        return player;
    }

    /**
     * Get the current room the player is in.
     *
     * @return the current Room object.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Uses MainGameLevelFactory to save all the completed room numbers and the seed of the map as a JSON file
     * which can be loaded when the load button is pressed.
     */
    public void saveMapData() {
        // Exports the rooms and map data into the file path below after Save button is pressed.
        String levelNum = "" + currentLevelNumber;
        levelFactory.saveMapData(MAP_SAVE_PATH, levelNum);
    }

    /**
     * Selects a new room based on the given room key.
     *
     * @param roomKey the key of the room to select.
     */
    private void selectRoom(String roomKey) {
        logger.info("Changing to room: {}", roomKey);
        Room newRoom = this.currentLevel.getRoom(roomKey);
        if (newRoom == null) {
            logger.error("Room \"{}\" not found!", roomKey);
            return;
        }
        this.currentRoom = newRoom;
        this.currentRoomName = this.currentRoom.getRoomName();

        // update the minimap
        if (minimapFactory != null) {
            // update minimap
            gameArea.updateMinimap(roomKey);
            minimapFactory.updateMinimap(roomKey);
        } else {
            logger.error("MinimapFactory is null when updating minimap");
        }

        this.spawnRoom = true;
    }

    /**
     * Changes the current room to a new room specified by the room key.
     *
     * @param roomKey the key of the room to change to.
     */
    public void changeRooms(String roomKey) {
        if (currentRoom != null) {
            this.currentRoom.removeRoom();
        }
        selectRoom(roomKey);

        // Play appropriate music based on room type
        if (this.currentRoom instanceof BossRoom) {
            getGameArea().playMusic(1);
        } else {
            getGameArea().playMusic(0);
        }
    }

    /**
     * Updates the level based on the current level number.
     */

    public void updateLevel() {
        logger.info("Changing to level: {}", currentLevelNumber);

        if (currentLevelNumber < 3) {
            // Create and load the new level
            this.currentLevel = this.levelFactory.create(currentLevelNumber);
            selectRoom(this.currentLevel.getStartingRoomKey());

            // Initialize minimap
            // Initialize minimap
            gameArea.generateMinimap(getCurrentLevel());
            this.minimapFactory = new MinimapFactory(getCurrentLevel(), 0.5f);
            MinimapComponent minimapComponent = minimapFactory.createMinimap();

            Entity minimap = new Entity();
            minimap.addComponent(minimapComponent);
            this.gameArea.spawnEntity(minimap);
        }
    }

    /**
     * Generate the corresponding player position to the door they stepped in.
     *
     * @param player The player entity to get the current position from.
     * @return The new player position based on the door entered.
     */
    private Vector2 getNewPlayerPosition(Entity player) {
        Vector2 curPos = player.getPosition();
        curPos.x = Math.round(curPos.x);
        curPos.y = Math.round(curPos.y);
        if (curPos.x == 0.0f && curPos.y == 5.0f) {
            return new Vector2(13, 5);
        }
        if (curPos.x == 14.0f && curPos.y == 5.0f) {
            return new Vector2(1, 5);
        }
        if (curPos.x == 7.0f && curPos.y == 0.0f) {
            return new Vector2(7, 9);
        }
        if (curPos.x == 7.0f && curPos.y == 10.0f) {
            return new Vector2(7, 1);
        }
        return new Vector2(7, 5);
    }

    /**
     * Spawns the current room if it hasn't been spawned yet.
     */
    public void spawnCurrentRoom() {
        if (!spawnRoom) {
            return;
        }

        if (ServiceLocator.getPhysicsService().getPhysics().getWorld().isLocked()) {
            logger.error("Physics is locked!");
            return;
        }

        logger.info("Spawning new room, {}", ServiceLocator.getEntityService());
        this.currentRoom.spawn(player, this.gameArea);

        Vector2 nextRoomPos = this.getNewPlayerPosition(player);
        player.setPosition(nextRoomPos.x, nextRoomPos.y);
        getGameArea().spawnEntity(player);

        if (!player.getComponent(InventoryComponent.class).getPets().isEmpty()) {
            if (ServiceLocator.getGameAreaService().getGameController().getCurrentRoom() instanceof EnemyRoom room) {
                List<Entity> enemies = room.getEnemies();
                player.getComponent(InventoryComponent.class).getPets().forEach(p -> p.setAggro(enemies));
            }
        }

        logger.info("Spawned new room, {}", ServiceLocator.getEntityService());
        spawnRoom = false;
    }


    /**
     * Gets the current level.
     *
     * @return the current Level object.
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Gets the GameArea.
     *
     * @return the current GameArea object.
     */
    public GameArea getGameArea() {
        return this.gameArea;
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public void changeLevel(int currentLevelNumber) {
        this.currentLevelNumber = currentLevelNumber;
    }
}


