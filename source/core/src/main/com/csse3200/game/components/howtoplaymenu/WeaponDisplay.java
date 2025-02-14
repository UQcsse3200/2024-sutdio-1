package com.csse3200.game.components.howtoplaymenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeaponDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(WeaponDisplay.class);
    private final GdxGame game;

    private Table rootTable;

    public WeaponDisplay(GdxGame game) {
        super();
        this.game = game;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Label title = new Label("Weapons", skin, "title");
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
        Table table = new Table();

        Label introLabel = new Label("The available weapons in the game are listed below:", skin);
        table.add(introLabel).colspan(2).pad(10).row();

        String[] animalDescriptions = {
                "Shotgun - Damage 10, Range 10, Fire Rate 5, Ammo 20, Max Ammo 20, Reload Time 3 seconds",
                "PlasmaBlaster - Damage 10, Range 10, Fire Rate 5, Ammo 20, Max Ammo 20, Reload Time 3 seconds",
                "SuperSoaker - Damage 10, Range 10, Fire Rate 5, Ammo 20, Max Ammo 20, Reload Time 3 seconds",
                "Knife - Damage 10, Range 4, Fire Rate 0 (instant use)",
                "Axe - Damage 50, Range 10, Fire Rate 1 (slow swing)"
        };

        String[] animalImagePaths = {
                "images/Weapons/Centered/Shotgun.png",
                "images/Weapons/Centered/PlasmaBlaster.png",
                "images/Weapons/Centered/SuperSoaker.png",
                "images/Weapons/Centered/Knife.png",
                "images/Weapons/Centered/Axe.png",

        };

        for (int i = 0; i < animalDescriptions.length; i++) {
            Image animalImage = new Image(new Texture(Gdx.files.internal(animalImagePaths[i])));
            Label animalLabel = new Label(animalDescriptions[i], skin);

            table.add(animalImage).size(100, 100).pad(10);
            table.add(animalLabel).pad(10).left().row();
        }
        Label bottomLabel = new Label("For melee weapons, press Space to use - Use arrow keys for ranged weapons.", skin);
        table.add(bottomLabel).colspan(2).pad(10).row();

        return table;
    }

    private Table makeMenuBtns() {
        TextButton exitBtn = new TextButton("Back", skin);

        exitBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Back button clicked");
                        game.setScreen(GdxGame.ScreenType.HOW_TO_PLAY);
                    }
                });

        Table table = new Table();
        table.add(exitBtn).expandX().left().pad(0f, 15f, 15f, 0f);
        return table;
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