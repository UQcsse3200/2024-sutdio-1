package com.csse3200.game.areas.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.utils.math.RandomUtils;
import com.csse3200.game.services.ServiceLocator;

/** Factory for creating game terrains. */
public class TerrainFactory {
  private static final GridPoint2 MAP_SIZE = new GridPoint2(15, 11);
  private final OrthographicCamera camera;

  private int currentLevel;
  private boolean isBossRoom = false;


  /**
   * Create a terrain factory with Orthogonal orientation
   *
   * @param cameraComponent Camera to render terrains to. Must be ortographic.
   */
  public TerrainFactory(CameraComponent cameraComponent) {
    this.camera = (OrthographicCamera) cameraComponent.getCamera();
    this.currentLevel = 0;
  }

  /**
   * Create a terrain factory with the default camera.
   */
  public TerrainFactory(){

    this(ServiceLocator.getRenderService().getCamera());
    this.currentLevel = 0;

  }

  /**
   * Create a terrain factory with the default camera.
   */
  public TerrainFactory(int currentLevel){

    this(ServiceLocator.getRenderService().getCamera());
    this.currentLevel = currentLevel;

  }

  /**
   * Create a terrain of the given type, using the orientation of the factory. This can be extended
   * to add additional game terrains.
   *
   * @param terrainType Terrain to create
   * @param isBossRoom is boss room
   * @return Terrain component which renders the terrain
   */
  public TerrainComponent createTerrain(TerrainType terrainType, boolean isBossRoom) {
    int tempLevel = (currentLevel+1);
    setBossRoom(isBossRoom);

    Skin skin = new Skin(Gdx.files.internal("skins/levels/level" + tempLevel + "/level" + tempLevel + "_skin.json"),
            ServiceLocator.getResourceService().getAsset("skins/levels/level" + tempLevel + "/level" + tempLevel + "_skin.atlas", TextureAtlas.class));

    switch (terrainType) {
      case ROOM1:
        TextureRegion tileMain = skin.getRegion("tile_middle_level" + tempLevel);
        TextureRegion tileLU = skin.getRegion("tile_1_level" + tempLevel);
        TextureRegion tileLD = skin.getRegion("tile_4_level" + tempLevel);
        TextureRegion tileRU = skin.getRegion("tile_2_level" + tempLevel);
        TextureRegion tileRD = skin.getRegion("tile_3_level" + tempLevel);
        TextureRegion tileL = skin.getRegion("tile_8_level" + tempLevel);
        TextureRegion tileR = skin.getRegion("tile_6_level" + tempLevel);
        TextureRegion tileU = skin.getRegion("tile_5_level" + tempLevel);
        TextureRegion tileD = skin.getRegion("tile_7_level" + tempLevel);
        TextureRegion tileB1 = skin.getRegion("tile_broken1_level" + tempLevel);
        TextureRegion tileB2 = skin.getRegion("tile_broken2_level" + tempLevel);
        TextureRegion tileB3 = skin.getRegion("tile_broken3_level" + tempLevel);
        TextureRegion tileStained = skin.getRegion("tile_blood_level" + tempLevel);
        return createRoomTerrain(1f, new TextureRegion[]{
                tileMain, tileLU, tileLD, tileRU, tileRD, tileL, tileR, tileU, tileD ,tileB1, tileB2, tileB3, tileStained});
      default:
        return null;
    }
  }


/**
 * Creates a TerrainComponent for a room using the provided tile set and world size.
 * @param tileWorldSize -the size of each tile in world units
 * @param tileSet -An array of TextureRegions representing different tile types.
 * */

  private TerrainComponent createRoomTerrain(float tileWorldSize, TextureRegion[] tileSet) {
    GridPoint2 tilePixelSize = new GridPoint2(tileSet[0].getRegionWidth(), tileSet[0].getRegionHeight());
    TiledMap tiledMap = createRoomTiles(tilePixelSize, tileSet);
    TiledMapRenderer renderer = createRenderer(tiledMap, tileWorldSize/tilePixelSize.x);
    return new TerrainComponent(camera, tiledMap, renderer, tileWorldSize);
  }

  private TiledMap createRoomTiles(GridPoint2 tileSize, TextureRegion[] tileSet) {
    TiledMap tiledMap = new TiledMap();

    TerrainTile mainTile = new TerrainTile(tileSet[0]);
    TerrainTile luTile = new TerrainTile(tileSet[1]);
    TerrainTile ldTile = new TerrainTile(tileSet[2]);
    TerrainTile ruTile = new TerrainTile(tileSet[3]);
    TerrainTile rdTile = new TerrainTile(tileSet[4]);
    TerrainTile lTile = new TerrainTile(tileSet[5]);
    TerrainTile rTile = new TerrainTile(tileSet[6]);
    TerrainTile uTile = new TerrainTile(tileSet[7]);
    TerrainTile dTile = new TerrainTile(tileSet[8]);
    TerrainTile b1Tile = new TerrainTile(tileSet[9]);
    TerrainTile b2Tile = new TerrainTile(tileSet[10]);
    TerrainTile b3Tile = new TerrainTile(tileSet[11]);
    TerrainTile stairStained = new TerrainTile(tileSet[12]);



    TiledMapTileLayer layer = new TiledMapTileLayer(MAP_SIZE.x, MAP_SIZE.y, tileSize.x, tileSize.y);

    // fill room tile
    fillTiles(layer, MAP_SIZE, new TerrainTile[]{
            mainTile, luTile, ldTile, ruTile, rdTile, lTile, rTile, uTile, dTile,
            b1Tile, b2Tile, b3Tile, stairStained
    });

    tiledMap.getLayers().add(layer);
    return tiledMap;
  }


  /** Fills the TiledMapTileLayer with tiles based on the provided tile list and map size.
   * his method handles the placement of different tile types, including boundary tiles,
   *  * broken tiles, and stained tiles.
   * @param layer
   * @param mapSize
   * @param tileList
   */

  private void fillTiles(
          TiledMapTileLayer layer, GridPoint2 mapSize, TerrainTile[] tileList) {
    for (int x = 0; x < mapSize.x; x++) {
      for (int y = 0; y < mapSize.y; y++) {
        if (!isBoundaryTile(x, y, mapSize)) {
          // adding broken tiles
          if (isBrokenTile()) {
            Cell cell = new Cell();
            cell.setTile(tileList[getBrokenTile()]);
            layer.setCell(x, y, cell);
          } else if (isStainedTile()) {
            Cell cell = new Cell();
            cell.setTile(tileList[12]);
            layer.setCell(x, y, cell);
          } else {
            // general tiles
            Cell cell = new Cell();
            cell.setTile(tileList[0]);
            layer.setCell(x, y, cell);
          }
        } else {
          Cell cell = new Cell();
          if (x == 0 && y == 0) {
            cell.setTile(tileList[2]);
          } else if (x == 0 && y == (mapSize.y-1)) {
            cell.setTile(tileList[1]);
          } else if (x == (mapSize.x-1) && y == 0) {
            cell.setTile(tileList[4]);
          } else if (x == (mapSize.x-1) && y == (mapSize.y-1)) {
            cell.setTile(tileList[3]);
          } else if (y > 0 && y < (mapSize.y-1) && x == 0) {
            cell.setTile(tileList[5]);
          } else if (y > 0 && y < (mapSize.y-1) && x == (mapSize.x-1)) {
            cell.setTile(tileList[6]);
          } else if (x > 0 && x < (mapSize.x-1) && y == 0) {
            cell.setTile(tileList[8]);
          } else if (x > 0 && x < (mapSize.x-1) && y == (mapSize.y-1)) {
            cell.setTile(tileList[7]);
          }
          layer.setCell(x, y, cell);
        }
      }
    }
  }

  /**
   * Determines if a given tile coordinate is on the boundary of the map
   * @param x - The x-coordinate of the tile.
   * @param y - The y-coordinate of the tile.
   * @param mapSize - The size of the map in grid coordinates.
   * @return true if tile is on boundary of map
   */

  private boolean isBoundaryTile(int x, int y, GridPoint2 mapSize) {
    return x == 0 || x == mapSize.x - 1 || y == 0 || y == mapSize.y - 1;
  }

  // Return true for broken tile (30% chance), false for general tile (70% chance)
  public boolean isBrokenTile() {
    // Generate a random number between 0 and 2
    int randomValue = RandomUtils.randomInt(0, 99);
    return randomValue < 15;
  }

  // Return random broken tile
  public int getBrokenTile() {
    return RandomUtils.randomInt(9, 11);
  }

  // Return true for broken tile (5% chance), false for general tile (95% chance)
  public boolean isStainedTile() {
    // Generate a random number between 0 and 2
    int randomValue = RandomUtils.randomInt(0, 99);
    return randomValue < 5;
  }

  /**
   * This enum should contain the different terrains in your game, e.g. forest, cave, home, all with
   * the same oerientation. But for demonstration purposes, the base code has the same level in 3
   * different orientations.
   */
  public enum TerrainType {
    ROOM1
  }

  /**
   * Creates a TiledMapRenderer for the given TiledMap with the specified tile scale.
   * @param tiledMap
   * @param tileScale
   * @return
   */
  private TiledMapRenderer createRenderer(TiledMap tiledMap, float tileScale) {
    return new OrthogonalTiledMapRenderer(tiledMap, tileScale);
  }

  public int getCurrentLevel() {
    return currentLevel;
  }

  public void setCurrentLevel(int currentLevel) {
    this.currentLevel = currentLevel;
  }

  public boolean isBossRoom() {
    return isBossRoom;
  }

  public void setBossRoom(boolean bossRoom) {
    isBossRoom = bossRoom;
  }
}




