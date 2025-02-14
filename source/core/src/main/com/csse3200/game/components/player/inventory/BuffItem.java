package com.csse3200.game.components.player.inventory;

import com.csse3200.game.entities.Entity;

/**
 * A class that represents an item that cannot be added to the inventory, and instead applies
 * effects immediately upon pickup
 */
public abstract class BuffItem implements Collectible {
    /**
     * Get the Type of this item. The type determines how it ought to be used by the player.
     *
     * @return the Type of this item
     */
    @Override
    public Type getType() {
        return Type.BUFF_ITEM;
    }

    /**
     * Return a string representation of this collectible that can be parsed by CollectibleFactory
     *
     * @return the string representation of this collectible.
     */
    @Override
    public String getSpecification() {
        return "buff:" + getBuffSpecification();
    }

    /**
     * Make the entity pick the item up, and apply effects immediately instead of being added to the inventory
     *
     * @param inventory The inventory to be put in.
     */
    @Override
    public void pickup(Inventory inventory) {
        inventory.getContainer(BuffItem.class).add(this);
        effect(inventory.getEntity());
    }

    @Override
    public void drop(Inventory inventory) {
        // do nothing buffs are permanent.
    }

    /**
     * Get the specification of this buff.
     *
     * @return the string representation of this buff.
     */
    public abstract String getBuffSpecification();

    /**
     * A method that applies the unique effect of this item, upon pickup
     *
     * @param entity The player entity
     */
    public abstract void effect(Entity entity);
}
