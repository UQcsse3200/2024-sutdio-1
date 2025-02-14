package com.csse3200.game.components.player.inventory.usables;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.player.inventory.UsableItem;
import com.csse3200.game.entities.Entity;

/**
 * The Medkit item class can be used by player to increase health,
 * by a large health boost of 100.
 */
public class MedKit extends UsableItem {
    private static final int LARGE_HEALTH_BOOST = 100;

    /**
     * Get the specification of this item.
     *
     * @return the string representation of this item.
     */
    @Override
    public String getItemSpecification() {
        return "medkit";
    }

    /**
     * Applies the Medkit to an entity, increasing its health by a large amount,
     * calls the increaseLargeBoost(entity) method
     *
     * @param entity to which Medkit item effect is applied to.
     */
    @Override
    public void apply(Entity entity) {
        increaseLargeBoost(entity);
    }

    /**
     * Returns name of item
     *
     * @return the item name
     */
    @Override
    public String getName() {
        return "Medkit";
    }

    /**
     * Return texture related with Medkit item
     *
     * @return texture representing icon of Medkit item
     */
    @Override
    public Texture getIcon() {
        return new Texture("images/items/med_kit.png");
    }

    /**
     * Get mystery box icon for this specific item
     *
     * @return mystery box icon
     */
    @Override
    public Texture getMysteryIcon() {
        return new Texture("images/items/mystery_box_green.png");
    }

    /**
     * Increases health by using entity's CombatStatsComponent to add Health
     *
     * @param entity whose health is increased.
     */
    public void increaseLargeBoost(Entity entity) {
        CombatStatsComponent combatStats = entity.getComponent(CombatStatsComponent.class);
        int currentHealth = combatStats.getHealth();
        int newHealth = Math.min(currentHealth + LARGE_HEALTH_BOOST, combatStats.getMaxHealth());
        combatStats.setHealth(newHealth);
    }
}