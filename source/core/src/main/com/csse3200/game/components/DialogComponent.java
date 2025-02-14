package com.csse3200.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.rendering.RenderComponent;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.components.NameComponent;

/**
 * This component shows a dialog box with entity image at the bottom left of the game area.
 */
public class DialogComponent extends RenderComponent {
    public static final int DIALOG_LAYER = 2;

    public static final float PADDING = 0.1f;
    private static float width = 0f;
    public static float height = 2.4f + PADDING * 2;
    public static final float OFFSET_Y = 1.5f;

    private static String text = "";
    private static String glyphText = "";
    private static float textLength = 0f;
    private static final float FRAMES_PER_CHAR = 5f;
    private static final float MAX_WIDTH = 15f;
    
    private int cooldownTime = 2;

    private static Texture texture;

    private NameComponent nameComponent;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;

    /**
     * Called when the entity is created and registered. Initializes the ShapeRenderer.
     */
    @Override
    public void create() {
        super.create();

        nameComponent = entity.getComponent(NameComponent.class);
        texture = new Texture(Gdx.files.internal("images/npc/"+nameComponent.getName()+".png"));

        shapeRenderer = new ShapeRenderer();

        UserSettings.get();

        fnt_18.setColor(Color.BLACK);

        layout = new GlyphLayout();
    }

    /**
     * creates a dialog box with given text and entity image
     *
     * @param newText The text to show in dialog box
     */
    public void showDialog(String newText) {
        text = newText;
        glyphText = "";
        textLength = 0f;
    }

    private void completeDialog() {
        glyphText = text;
        layout.setText(fnt_18, glyphText, Color.WHITE, MAX_WIDTH/projectionFactor, Align.left, true);
        width = layout.width * projectionFactor + PADDING * 2;
        height = layout.height * projectionFactor + PADDING * 4;
        textLength = text.length();
        cooldownTime = 2;
    }

    //returns true if dialog is complete and dismisses the dialog else completes it
    public boolean dismissDialog() {
        if (glyphText.length() == text.length() && cooldownTime == 0) {
            text = "";
            return true;
        } else {
            completeDialog();
            return false;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!text.isEmpty()) {
            if (glyphText.length() < text.length()) {
                textLength += 1 / FRAMES_PER_CHAR;
                if ((int) textLength > glyphText.length()) {
                    glyphText = text.substring(0, (int) textLength);
                    layout.setText(fnt_18, glyphText, Color.WHITE, MAX_WIDTH/projectionFactor, Align.left, true);
                    width = layout.width * projectionFactor + PADDING * 2;
                    height = layout.height * projectionFactor + PADDING * 4;
                }
            }else
                if(cooldownTime > 0)
                    cooldownTime--;
            batch.end();

            // Set up the projection matrix for rendering
            Matrix4 projectionMatrix = batch.getProjectionMatrix().cpy();
            shapeRenderer.setProjectionMatrix(projectionMatrix);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Calculate position
            float x = 0f;
            float y = -fnt_18.getCapHeight()*2f*projectionFactor-PADDING*2+OFFSET_Y;

            float overflowWidth = 0f;
            if(x+width > MAX_WIDTH)
                overflowWidth = x+width-MAX_WIDTH;

            float overflowHeight = 0f;
            if(layout.height > fnt_18.getCapHeight())
                overflowHeight = layout.height-fnt_18.getCapHeight();

            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.rect(x - overflowWidth, y, width, height);

            shapeRenderer.end();

            batch.begin();
            batch.setProjectionMatrix(projectionMatrix.cpy().scale(projectionFactor, projectionFactor, 1));
            batch.draw(texture, (x - overflowWidth) / projectionFactor, (y + PADDING*2) / projectionFactor + fnt_18.getCapHeight()*1.5f + overflowHeight);
            fnt_18.draw(batch, layout, (x + PADDING - overflowWidth) / projectionFactor, (y + PADDING) / projectionFactor + fnt_18.getCapHeight()*1.5f + overflowHeight);
            batch.setProjectionMatrix(projectionMatrix);
        }
    }

    @Override
    public int getLayer() {
        return DIALOG_LAYER;
    }

    @Override
    public float getZIndex() {
        // The smaller the Y value, the higher the Z index, so that closer entities are drawn in front
        return 1f;
    }

    /**
     * Disposes of the resources used by this component, specifically the ShapeRenderer.
     */
    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}