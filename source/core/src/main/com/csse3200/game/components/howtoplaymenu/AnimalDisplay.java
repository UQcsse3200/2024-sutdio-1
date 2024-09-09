package com.csse3200.game.components.howtoplaymenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.GdxGame;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.utils.StringDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimalDisplay extends UIComponent{
    private static final Logger logger = LoggerFactory.getLogger(AnimalDisplay.class);
    private final GdxGame game;

    private Table rootTable;
    private TextField fpsText;
    private CheckBox fullScreenCheck;
    private CheckBox vsyncCheck;
    private Slider uiScaleSlider;
    private SelectBox<StringDecorator<Graphics.DisplayMode>> displayModeSelect;

    public AnimalDisplay(GdxGame game) {
        super();
        this.game = game;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Label title = new Label("Animals", skin, "title");
        Table howToPlayTable = makeHowToPlayTable();
        Table menuBtns = makeMenuBtns();

        rootTable = new Table();
        rootTable.setFillParent(true);

        rootTable.add(title).expandX().top().padTop(20f);

        rootTable.row().padTop(30f);
        rootTable.add(howToPlayTable).expandX().expandY();

        rootTable.row();
        rootTable.add(menuBtns).fillX();

        stage.addActor(rootTable);
    }

    private Table makeHowToPlayTable() {
        Label instruction = new Label("There are x types of animals: ", skin);

        String[][] paragraphs = {{
                "Beast Breakout is a top-down dungeon crawler game, presented using "
                        + "two-dimensional sprites, in which the player controls",
                "an unnamed character in a non-specific facility."
        }, {
                "On each floor of the facility, the player must fight enraged animals in a room "
                        + "before continuing onto the next room. This is",
                "most commonly done by the character's melee or ranged weapon in the style of a "
                        + "twin-stick shooter."
        }, {
                "Other methods of defeating enemies become possible as the character gains "
                        + "power-ups, items that are automatically worn",
                "by the player-character when picked up that can alter the character's core "
                        + "attributes, such as increasing health or the",
                "strength of their weapons, or cause additional side effects."
        }, {
                "When the player loses all of their health the game ends in permadeath and the "
                        + "player must start over from a freshly-",
                "generated dungeon. Each floor of the dungeon includes a boss which the player "
                        + "must defeat before continuing to the next level."
        }
        };

        // Position components on the table
        Table table = new Table();
        table.add(instruction).left().padRight(10f);
        table.row().padTop(40f);

        for (String[] paragraph : paragraphs) {
            for (int i = 0; i < paragraph.length; i++) {
                String line = paragraph[i];
                Label label = new Label(line, skin);
                table.add(label).left().expandX();

                // Different padding for end of paragraph
                boolean lastLine = (i + 1 == paragraph.length);
                table.row().padTop(lastLine ? 40f : 10f);
            }
        }

        // todo look into word wrap so we don't need this many labels

        return table;
    }

    private StringDecorator<Graphics.DisplayMode> getActiveMode(Array<StringDecorator<Graphics.DisplayMode>> modes) {
        Graphics.DisplayMode active = Gdx.graphics.getDisplayMode();

        for (StringDecorator<Graphics.DisplayMode> stringMode : modes) {
            Graphics.DisplayMode mode = stringMode.object;
            if (active.width == mode.width
                    && active.height == mode.height
                    && active.refreshRate == mode.refreshRate) {
                return stringMode;
            }
        }
        return null;
    }

    private Array<StringDecorator<Graphics.DisplayMode>> getDisplayModes(Graphics.Monitor monitor) {
        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes(monitor);
        Array<StringDecorator<Graphics.DisplayMode>> arr = new Array<>();

        for (Graphics.DisplayMode displayMode : displayModes) {
            arr.add(new StringDecorator<>(displayMode, this::prettyPrint));
        }

        return arr;
    }

    private String prettyPrint(Graphics.DisplayMode displayMode) {
        return displayMode.width + "x" + displayMode.height + ", " + displayMode.refreshRate + "hz";
    }

    private Table makeMenuBtns() {
        TextButton exitBtn = new TextButton("Back", skin);

        exitBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Back button clicked");
                        exitMenu();
                    }
                });

        Table table = new Table();
        table.add(exitBtn).expandX().left().pad(0f, 15f, 15f, 0f);
        return table;
    }

    private void applyChanges() {
        UserSettings.Settings settings = UserSettings.get();

        Integer fpsVal = parseOrNull(fpsText.getText());
        if (fpsVal != null) {
            settings.fps = fpsVal;
        }
        settings.fullscreen = fullScreenCheck.isChecked();
        settings.uiScale = uiScaleSlider.getValue();
        settings.displayMode = new UserSettings.DisplaySettings(displayModeSelect.getSelected().object);
        settings.vsync = vsyncCheck.isChecked();

        UserSettings.set(settings, true);
    }

    private void exitMenu() {
        game.setScreen(GdxGame.ScreenType.HOW_TO_PLAY);
    }

    private Integer parseOrNull(String num) {
        try {
            return Integer.parseInt(num, 10);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // draw is handled by the stage
    }

    @Override
    public void update() {
        stage.act(ServiceLocator.getTimeSource().getDeltaTime());
    }

    @Override
    public void dispose() {
        rootTable.clear();
        super.dispose();
    }
}