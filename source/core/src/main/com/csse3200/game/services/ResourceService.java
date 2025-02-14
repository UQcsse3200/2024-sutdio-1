package com.csse3200.game.services;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Null;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.files.UserSettings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for loading resources, e.g. textures, texture atlases, sounds, music, etc. Add new load
 * methods when new types of resources are added to the game.
 */
public class ResourceService implements Disposable {

  private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
  private final AssetManager assetManager;
  private final Settings settings;
  /**
   * Path to current playing music or null if none is playing.
   */
  private @Null String currentMusic = null;

  private final Map<String, Integer> referenceCounts = new HashMap<>();

  public ResourceService() {
    this(new AssetManager());
  }

  /**
   * Initialise this ResourceService to use the provided AssetManager.
   * @param assetManager AssetManager to use in this service.
   * @requires assetManager != null
   */
  public ResourceService(AssetManager assetManager) {
    this(assetManager, UserSettings.get());
  }

  public ResourceService(Settings settings) {
    this(new AssetManager(), settings);
  }

  public ResourceService(AssetManager assetManager, Settings settings) {
    this.assetManager = assetManager;
    this.settings = settings;
  }

  /**
   * Load an asset from a file.
   * @param filename Asset path
   * @param type     Class to load into
   * @param <T>      Type of class to load into
   * @return Instance of class loaded from path
   * @see AssetManager#get(String, Class)
   */
  public <T> T getAsset(String filename, Class<T> type) {
    return assetManager.get(filename, type);
  }

  /**
   * Check if an asset has been loaded already
   * @param resourceName path of the asset
   * @param type Class type of the asset
   * @param <T> Type of the asset
   * @return true if asset has been loaded, false otherwise
   * @see AssetManager#contains(String)
   */
  public <T> boolean containsAsset(String resourceName, Class<T> type) {
    return assetManager.contains(resourceName, type);
  }

  /**
   * Returns the loading completion progress as a percentage.
   *
   * @return progress
   */
  public int getProgress() {
    return (int) (assetManager.getProgress() * 100);
  }

  /**
   * Blocking call to load all assets.
   *
   * @see AssetManager#finishLoading()
   */
  public void loadAll() {
    logger.debug("Loading all assets");
    try {
      assetManager.finishLoading();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * Loads assets for the specified duration in milliseconds.
   *
   * @param duration duration to load for
   * @return finished loading
   * @see AssetManager#update(int)
   */
  public boolean loadForMillis(int duration) {
    logger.debug("Loading assets for {} ms", duration);
    try {
      return assetManager.update(duration);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return assetManager.isFinished();
  }

  /**
   * Clears all loaded assets and assets in the preloading queue.
   *
   * @see AssetManager#clear()
   */
  public void clearAllAssets() {
    logger.debug("Clearing all assets");
    assetManager.clear();
  }

  /**
   * Loads a single asset into the asset manager.
   *
   * @param assetName asset name
   * @param type      asset type
   * @param <T>       type
   */
  private <T> void loadAsset(String assetName, Class<T> type) {
    logger.debug("Loading {}: {}", type.getSimpleName(), assetName);
    try {
      if (!assetManager.isLoaded(assetName)) {
        referenceCounts.put(assetName, 0);
      }
      assetManager.load(assetName, type);
      referenceCounts.put(assetName, referenceCounts.get(assetName) + 1);
    } catch (Exception e) {
      logger.error("Could not load {}: {}", type.getSimpleName(), assetName);
    }
  }

  /**
   * Loads multiple assets into the asset manager.
   *
   * @param assetNames list of asset names
   * @param type       asset type
   * @param <T>        type
   */
  private <T> void loadAssets(String[] assetNames, Class<T> type) {
    for (String resource : assetNames) {
      loadAsset(resource, type);
    }
  }

  /**
   * Loads a list of texture assets into the asset manager.
   *
   * @param textureNames texture filenames
   */
  public void loadTextures(String[] textureNames) {
    loadAssets(textureNames, Texture.class);
  }

  /**
   * Loads a list of texture atlas assets into the asset manager.
   *
   * @param textureAtlasNames texture atlas filenames
   */
  public void loadTextureAtlases(String[] textureAtlasNames) {
    loadAssets(textureAtlasNames, TextureAtlas.class);
  }

  /**
   * Loads a list of font assets into the asset manager.
   *
   * @param fontNames font filenames
   */
  public void loadFonts(String[] fontNames) {
    loadAssets(fontNames, BitmapFont.class);
  }


  /**
   * Loads a list of sounds into the asset manager.
   *
   * @param soundNames sound filenames
   */
  public void loadSounds(String[] soundNames) {
    loadAssets(soundNames, Sound.class);
  }

  /**
   * Loads a list of music assets into the asset manager.
   *
   * @param musicNames music filenames
   */
  public void loadMusic(String[] musicNames) {
    loadAssets(musicNames, Music.class);
  }

  public void unloadAssets(String[] assetNames) {
    for (String assetName : assetNames) {
      logger.debug("Unloading {}", assetName);
      try {
        if (referenceCounts.getOrDefault(assetName, 0) > 1) {
          referenceCounts.put(assetName, referenceCounts.get(assetName) - 1);
        } else {
          assetManager.unload(assetName);
          referenceCounts.remove(assetName);
        }
      } catch (Exception e) {
        logger.error("Could not unload {}", assetName);
      }
    }
  }
    /**
     * Play a sound effect. The volume is determined by {@link UserSettings}. No sound is played
     * when mute is on.
     *
     * @param soundName The path of the asset relative to the assets folder.
     */
    public void playSound(String soundName) {
        if (!settings.mute) {
            Sound sound = getAsset(soundName, Sound.class);
            sound.play(settings.soundVolume);
        }
    }

  /**
   * Play music. The volume is determined by {@link UserSettings}. No music is played
   * when mute is on. If any other music was already playing, it is stopped.
   *
   * @param musicName The path of the music relative to the assets folder.
   * @param loop true to loop the music, false for single play-through.
   *
   * @return the chosen music asset or null if it wasn't loaded.
   */
  public @Null Music playMusic(String musicName, boolean loop) {
    if (!containsAsset(musicName, Music.class)) {
      logger.error("Music {} not loaded", musicName);
      return null;
    }
    Music music = getAsset(musicName, Music.class);
    if (currentMusic != null && currentMusic.equals(musicName)) {
      // This music is already playing
      return music;
    }
    stopCurrentMusic();
    currentMusic = musicName;
    if (!settings.mute) {
      music.setLooping(loop);
      music.setVolume(UserSettings.get().musicVolume);
      music.play();
    }
    return music;
  }

  /**
   * Stop the currently playing music.
   */
  public void stopCurrentMusic() {
    if (currentMusic == null) {
      logger.info("No music playing, stopping music has no effect");
      return;
    }
    if (containsAsset(currentMusic, Music.class)) {
      getAsset(currentMusic, Music.class).stop();
    }
  }

  @Override
  public void dispose() {
    assetManager.clear();
  }
}
