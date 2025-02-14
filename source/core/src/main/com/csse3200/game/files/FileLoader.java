package com.csse3200.game.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for reading Java objects from JSON files.
 *
 * <p>A generic method is provided already, but methods for reading specific classes can be added
 * for more control.
 */
public class FileLoader {
  private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);
  static final Json json = new Json();
  private static final String saveRootPath = switch (System.getProperty("os.name")
          .substring(0, 3).toLowerCase()){
    case "win" -> "My Documents/Beast Breakout/";
    case "mac" -> "Documents/Beast Breakout/";
    default -> "Beast Breakout/";
  };

  /**
   * Read generic Java classes from a JSON file. Properties in the JSON file will override class
   * defaults.
   *
   * @param type class type
   * @param filename file to read from
   * @param <T> Class type to read JSON into
   * @return instance of class, may be null
   */
  public static <T> T readClass(Class<T> type, String filename) {
    return readClass(type, filename, Location.INTERNAL);
  }

  /**
   * Read generic Java classes from a JSON file. Properties in the JSON file will override class
   * defaults.
   *
   * @param type class type
   * @param filename file to read from
   * @param location File storage type. See
   *     <a href="https://libgdx.com/wiki/file-handling#file-storage-types">github</a>
   * @param <T> Class type to read JSON into
   * @return instance of class, may be null
   */
  public static <T> T readClass(Class<T> type, String filename, Location location) {
    logger.debug("Reading class {} from {}", type.getSimpleName(), filename);
    FileHandle file = getFileHandle(filename, location);
    if (file == null) {
      logger.error("Failed to create file handle for {}", filename);
      return null;
    }

    T object;
    try {
      object = json.fromJson(type, file);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
    if (object == null) {
      String path = file.path();
      logger.error("Error creating {} class instance from {}", type.getSimpleName(), path);
    }
    return object;
  }

  /**
   * Read a Map&lt;String, T&gt; from a JSON file.
   *
   * @param valueType Class type of the map's values
   * @param filename  File to read from
   * @param <T>       Class type to read JSON into
   * @return Map of the JSON data
   */
  public static <T> Map<String, T> readMap(Class<T> valueType, String filename) {
    return readMap(valueType, filename, Location.INTERNAL);
  }

  /**
   * Read a Map&lt;String, T&gt; from a JSON file.
   *
   * @param valueType Class type of the map's values
   * @param filename  File to read from
   * @param location  File storage type
   * @param <T>       Class type to read JSON into
   * @return Map of the JSON data
   */
  public static <T> Map<String, T> readMap(Class<T> valueType, String filename, Location location) {
    logger.debug("Reading Map<String, {}> from {}", valueType.getSimpleName(), filename);
    FileHandle file = getFileHandle(filename, location);
    if (file == null) {
      logger.error("Failed to create file handle for {}", filename);
      return null;
    }

    Map<String, T> map = new HashMap<>();
    try {
      JsonReader jsonReader = new JsonReader();
      JsonValue root = jsonReader.parse(file);

      for (JsonValue entry = root.child; entry != null; entry = entry.next) {
        logger.debug("Parsing entry for key: {} - {}", entry.name, entry);
        String key = entry.name;
        T value = json.readValue(valueType, entry);
        map.put(key, value);
      }
    } catch (Exception e) {
      logger.error("Error reading Map from {}: {}", filename, e.getMessage());
      return null;
    }
    return map;
  }


  /**
   * Write generic Java classes to a JSON file.
   *
   * @param object Java object to write.
   * @param filename File to write to.
   */
  public static void writeClass(Object object, String filename) {
    writeClass(object, filename, Location.EXTERNAL);
  }

  /**
   * Write generic Java classes to a JSON file.
   *
   * @param object Java object to write.
   * @param filename File to write to.
   * @param location File storage type. See
   *     <a href="https://github.com/libgdx/libgdx/wiki/File-handling#file-storage-types">github</a>
   */
  public static void writeClass(Object object, String filename, Location location) {
    logger.debug("Writing class {} to {}", object.getClass().getSimpleName(), filename);
    FileHandle file = getFileHandle(filename, location);
    assert file != null;
    file.writeString(json.prettyPrint(object), false);
  }

  private static FileHandle getFileHandle(String filename, Location location) {
      return switch (location) {
          case CLASSPATH -> Gdx.files.classpath(filename);
          case INTERNAL -> Gdx.files.internal(filename);
          case LOCAL -> Gdx.files.local(filename);
          case EXTERNAL -> Gdx.files.external(saveRootPath + filename);
          case ABSOLUTE -> Gdx.files.absolute(filename);
      };
  }

  /**
   * Check if file exists in the given location.
   * @param filename path of the file
   * @param location file location
   * @return true if the file exists, false otherwise
   */
  public static boolean fileExists(String filename, Location location) {
    return getFileHandle(filename, location).exists();
  }

  public enum Location {
    CLASSPATH,
    INTERNAL,
    LOCAL,
    EXTERNAL,
    ABSOLUTE
  }
}
