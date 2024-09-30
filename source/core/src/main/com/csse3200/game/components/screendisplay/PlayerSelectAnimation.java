package com.csse3200.game.components.screendisplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.csse3200.game.rendering.AnimationRenderComponent;

/**
 * This class is sued to control the animation rendering for each
 * player on the Select Screen.
 */
public class PlayerSelectAnimation extends Actor {
    private final AnimationRenderComponent animator;
    private final PlayerNum player;

    public enum PlayerNum {
        Player1,
        Player2,
        Player3,
        Bear,
        Player4
    }

    public PlayerSelectAnimation(AnimationRenderComponent animator, String textureAtlas) {
        this.animator = animator;
        this.player = mapTextureAtlasToPlayerNum(textureAtlas);
        initializeAnimations();
    }

    // Assign player number to the texture atlas
    private PlayerNum mapTextureAtlasToPlayerNum(String textureAtlas) {
        return switch (textureAtlas) {
            case "images/player/player.atlas" -> PlayerNum.Player1;
            case "images/player/homeless1.atlas" -> PlayerNum.Player2;
            case "images/npc/bear/bear.atlas" -> PlayerNum.Bear;
            case "images/player/homeless2.atlas" -> PlayerNum.Player3;
            case "images/player/homeless3.atlas" -> PlayerNum.Player4;
            default -> throw new IllegalArgumentException("Unknown texture atlas: " + textureAtlas);
        };
    }

    // Add texture region relative to each player
    private void initializeAnimations() {
        animator.addAnimation("idle", 0.2f, Animation.PlayMode.LOOP);
        switch (player) {
            case Player2:
                animator.addAnimation("Special", 0.2f, Animation.PlayMode.LOOP);
                break;
            case Player3:
                animator.addAnimation("Run", 0.2f, Animation.PlayMode.LOOP_PINGPONG);
                break;
            case Player4:
                animator.addAnimation("Attack_1", 0.2f, Animation.PlayMode.LOOP_RANDOM);
                break;
            case Bear:
                animator.addAnimation("idle_right", 0.5f, Animation.PlayMode.LOOP);
                animator.addAnimation("attack_right", 0.5f, Animation.PlayMode.LOOP);
                break;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animator.update(delta); // Update the animator state
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Set the batch color for transparency
        batch.setColor(1f, 1f, 1f, parentAlpha);

        // Get the current frame from the animator
        TextureRegion currentFrame = animator.getCurrentFrame();
        if (currentFrame != null) {
            // Calculate the center position
            float centerX = (getX() + (getWidth() / 2)) - ((float) currentFrame.getRegionWidth() / 2);
            float centerY = getY();

            if (player == PlayerNum.Player1) {
                batch.draw(currentFrame, centerX, centerY);
            } else {
                // Draw the current frame at the actor's position and size, centered
                batch.draw(currentFrame, centerX - 50, centerY, 250, 250);
            }
        }

        // Reset batch color
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void startAnimation() {
        String animationName = switch (player) {
            case Player4 -> "Attack_1";
            case Player2 -> "Special";
            case Player3 -> "Run";
            case Bear-> "idle_right";
            default -> "idle";
        };
        animator.startAnimation(animationName);
    }
}