package com.csse3200.game.components.screendisplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.components.player.PlayerFactoryFactory;
import com.csse3200.game.entities.configs.PlayerConfig;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.csse3200.game.screens.PlayerSelectScreen.BG_IMAGE;
import static com.csse3200.game.services.ServiceLocator.getResourceService;

/**
 * Creates the ui elements and functionality for the player select screen's ui.
 */
public class PlayerSelectDisplay extends UIComponent {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSelectDisplay.class);
    private static final float Z_INDEX = 2f;
    private final GdxGame game;
    private Table rootTable;
    private TextField seedInputField;
    private static final float ROOT_PADDING = 10f;
    private static final float STAT_TABLE_PADDING = 2f;
    private static final float PROPORTION = 0.8f;

    private final PlayerFactoryFactory playerFactoryFactory = new PlayerFactoryFactory();

    /**
     * Make the component.
     * @param game the overarching game, needed so that buttons can trigger navigation through
     *             screens
     */
    public PlayerSelectDisplay(GdxGame game) {
        this.game = game;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    /**
     * Populate the stage with player animations and buttons to select them.
     */
    private void addActors() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        float percentWidth = PROPORTION / 6;
        Value valueWidth = Value.percentWidth(percentWidth, rootTable);
        rootTable.defaults()
                .pad(ROOT_PADDING).fill()
                .width(valueWidth);

        Map<String, PlayerConfig> configs = playerFactoryFactory.getOptions();

        rootTable.row().height(valueWidth); // make each image cell square
        configs.forEach((filename, config) -> {
            String textureAtlasFilename = config.textureAtlasFilename;

            // Create new AnimationRenderComponent to control animations for each player
            AnimationRenderComponent animator =
                    new AnimationRenderComponent(new TextureAtlas(textureAtlasFilename));

            PlayerSelectAnimation animatedImage = new PlayerSelectAnimation(animator, config.textureAtlasFilename);
            animatedImage.startAnimation();

            rootTable.add(animatedImage);
        });

        // Add stat bars
        rootTable.row();
        // calculate highest health/speed out of all players
        int maxHealth = configs.values().stream()
                .map(player -> player.health)
                .max(Integer::compareTo).orElseThrow();
        float maxSpeed = configs.values().stream()
                .map(player -> player.speed.len())
                .max(Float::compareTo).orElseThrow();
        configs.forEach((filename, config) -> {
            Table statTable = new Table();
            statTable.defaults().pad(STAT_TABLE_PADDING).fill();
            statTable.columnDefaults(1).growX().minWidth(0); // stat bars
            addStat(statTable, "HLTH", config.health, maxHealth);
            addStat(statTable, "SPD", config.speed.len(), maxSpeed);
            rootTable.add(statTable);
        });

        // Add buttons to choose each player
        rootTable.row();
        configs.forEach((filename, config) -> {
            TextButton button = new TextButton("%s".formatted(config.name), skin, "action");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    logger.debug("Button to select {} pressed", filename);
                    playerSelected(filename);
                }
            });
            button.getLabel().setWrap(true);
            rootTable.add(button);
            button.getLabel().setFontScale((float) Gdx.graphics.getWidth() / 1600);
        });

        // Add seed input field at the bottom
        rootTable.row().padTop(20);
        Label seedLabel = new Label("Enter Seed:", skin);
        seedInputField = new TextField("", skin);
        rootTable.add(seedLabel).colspan(configs.size());
        rootTable.row();
        rootTable.add(seedInputField).colspan(configs.size()).fillX().padBottom(20);

        Image bgImage = new Image(getResourceService().getAsset(BG_IMAGE, Texture.class));
        bgImage.setFillParent(true);

        stage.addActor(bgImage);
        stage.addActor(rootTable);
    }

    /**
     * Add a row in the stat table (has stat name and stat bar).
     * @param table The stat table to add the stat to.
     * @param name The name of the stat (short and capitalised).
     * @param value The value of this stat.
     * @param maxValue The max possible value of this stat.
     */
    private void addStat(Table table, String name, float value, float maxValue) {
        table.row();
        Label statName = new Label(name, skin);
        table.add(statName);
        ProgressBar statBar = new ProgressBar(0, maxValue, 0.1f, false, skin);
        statBar.setAnimateDuration(.6f);
        statBar.setValue(value);
        table.add(statBar);
    }

    private void playerSelected(String name) {
        // Check if seed input field is empty, and use default "seed" if empty
        String seed = seedInputField.getText().isEmpty() ? "seed" : seedInputField.getText();
        logger.info("Player chosen: {} with seed: {}", name, seed);

        // Assign selected player and seed to game options
        game.gameOptions.playerFactory = playerFactoryFactory.create(name);
        game.gameOptions.seed = seed;

        // Fetch user settings to check if cutscene is enabled
        UserSettings.Settings currentSettings = UserSettings.get();
        boolean cutsceneEnabled = currentSettings.enableCutscene;

        // Redirect to cutscene or main game based on the cutscene setting
        if (cutsceneEnabled) {
            game.setScreen(ScreenType.CUTSCENE); // If cutscene is enabled
        } else {
            game.setScreen(ScreenType.MAIN_GAME); // If cutscene is disabled
        }
    }

    @Override
    public void dispose() {
        rootTable.clear();
        super.dispose();
    }

    @Override
    public float getZIndex() {
        return Z_INDEX;
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // draw is handled by stage
    }
}