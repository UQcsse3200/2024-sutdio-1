package com.csse3200.game.components.player.inventory;

import com.csse3200.game.components.player.ShieldComponent;
import com.csse3200.game.components.player.inventory.usables.ShieldPotion;
import com.csse3200.game.entities.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShieldPotionTest {

    ShieldPotion potion = new ShieldPotion();
    Entity entity = new Entity().addComponent(new ShieldComponent());

    @Test
    public void testGetName() {
        assertEquals("Shield Potion", potion.getName());
    }

    @Test
    public void testGetItemSpecification() {
        assertEquals("shieldpotion", potion.getItemSpecification());
    }

    @Test
    public void testGetSpecification() {
        assertEquals("item:shieldpotion", potion.getSpecification());
    }

    @Test
    public void testApply() {
        assertEquals(0, potion.getCharges());
        potion.apply(entity);
        assertEquals(2, potion.getCharges());
        assertTrue(entity.getComponent(ShieldComponent.class).isActive());
    }

    @Test
    public void testNegateHit() {
        potion.apply(entity);
        entity.getEvents().trigger("hit");
        assertEquals(1, potion.getCharges());
        assertTrue(entity.getComponent(ShieldComponent.class).isActive());
        entity.getEvents().trigger("hit");
        assertEquals(0, potion.getCharges());
        entity.getEvents().trigger("hit");
        assertEquals(0, potion.getCharges());
        assertFalse(entity.getComponent(ShieldComponent.class).isActive());
    }

    @Test
    public void testNegateHitWhenInactive() {
        entity.getEvents().trigger("hit");
        assertEquals(0, potion.getCharges());
    }

}