package com.csse3200.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.player.PlayerActions;
import com.csse3200.game.components.player.PlayerConfigComponent;
import com.csse3200.game.components.player.inventory.*;
import com.csse3200.game.components.player.inventory.buffs.Armor;
import com.csse3200.game.components.player.inventory.buffs.DamageBuff;
import com.csse3200.game.components.player.inventory.weapons.MeleeWeapon;
import com.csse3200.game.components.player.inventory.weapons.RangedWeapon;
import com.csse3200.game.entities.configs.PlayerConfig;
import com.csse3200.game.extensions.GameExtension;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * As gdx files are not initialise properly in test environment, instead of
 * testing whether the file writes into a json file, code's ability to store
 * player's component into configuration is tested
 */
@ExtendWith(GameExtension.class)
public class PlayerConfigGeneratorTest {
    Entity player;
    PlayerConfigGenerator generator = new PlayerConfigGenerator();
    InventoryComponent inventoryComponent;
    CombatStatsComponent statsComponent;
    CoinsComponent coinsComponent;
    PlayerActions playerActions;

    @Before
    public void setUp() {
        player = new Entity();
        inventoryComponent = new InventoryComponent();
        statsComponent = new CombatStatsComponent(100, 100,
                30, true, 0, 0,  true, 5);
        playerActions = new PlayerActions();

        player.addComponent(inventoryComponent).addComponent(statsComponent).addComponent(playerActions);

        coinsComponent = new CoinsComponent();

        player.addComponent(new PlayerConfigComponent(new PlayerConfig())).addComponent(coinsComponent);
    }

    /**
     * Test with default player attributes
     */
    @Test
    public void testInit() {
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(30, playerConfig.baseAttack);
        assertEquals(100, playerConfig.health);
        assertEquals(0, playerConfig.items.length);
    }

    /**
     * Test with customised Combat stats component
     */
    @Test
    public void testCustomCombatStat() {
        statsComponent.setHealth(50);
        statsComponent.setBaseAttack(20);
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(50, playerConfig.health);
        assertEquals(20, playerConfig.baseAttack);
    }

    @Test
    public void testCoins() {
        coinsComponent.setCoins(10);
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(10, playerConfig.coins);

    }

//    @Test
//    public void testSpeed() {
//        playerActions.setSpeed(new Vector2(5f, 5f));
//        PlayerConfig playerConfig = generator.savePlayerState(player);
//        assertEquals(new Vector2(5f, 5f), playerConfig.speed);
//    }

    /**
     * Test saved player's melee weapon
     */
    @Test
    public void testMeleeWeapon() {

        inventoryComponent.pickup(new MeleeWeapon() {
            @Override
            public void attack() {
            }

            @Override
            public String getName() {
                return "Knife";
            }

            @Override
            public Texture getIcon() {
                return null;
            }
        });

        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals("melee:Knife", playerConfig.melee);
    }


    /**
     * Test player's saved Ranged weapon
     */
    @Test
    public void testRangedWeapon() {
        inventoryComponent.pickup(new RangedWeapon() {

            @Override
            public void shoot(Vector2 direction) {

            }

            @Override
            public String getName() {
                return "Shotgun";
            }

            @Override
            public Texture getIcon() {
                return null;
            }
        });
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals("ranged:Shotgun", playerConfig.ranged);
    }

    /**
     * Test player with no weapon
     */
    @Test
    public void testNoWeapon() {
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals("", playerConfig.ranged);
        assertEquals("", playerConfig.melee);
    }

    @Test
    public void testNoInventory() {
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(0, playerConfig.items.length);
    }

    /**
     * Test player with only one item collected
     */
    @Test
    public void testOneItemInventory() {
        inventoryComponent.pickup(new UsableItem() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public Texture getIcon() {
                return null;
            }

            @Override
            public String getSpecification() {
                return "Health Potion";
            }

            @Override
            public String getItemSpecification() {
                return "";
            }

            @Override
            public void apply(Entity entity) {

            }
        });
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals("Health Potion", playerConfig.items[0]);
        assertEquals(1, playerConfig.items.length);
    }

    /**
     * Test player with multiple items collected
     */
    @Test
    public void testMultipleItemsInventory() {
        // add a new item to player's inventory
        inventoryComponent.pickup(new UsableItem() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public Texture getIcon() {
                return null;
            }

            @Override
            public String getSpecification() {
                return "Energy bar";
            }

            @Override
            public String getItemSpecification() {
                return "";
            }

            @Override
            public void apply(Entity entity) {

            }
        });
        inventoryComponent.pickup(new UsableItem() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public Texture getIcon() {
                return null;
            }

            @Override
            public String getSpecification() {
                return "Food";
            }

            @Override
            public String getItemSpecification() {
                return "";
            }

            @Override
            public void apply(Entity entity) {

            }
        });

        PlayerConfig playerConfig = generator.savePlayerState(player);
        String[] expected = {"Energy bar", "Food"};
        assertArrayEquals(expected, playerConfig.items);
    }

    /**
     * Test saving player's max health
     */
    @Test
    public void testMaxHealth() {
        statsComponent.setMaxHealth(120);
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(120, playerConfig.maxHealth);
    }

    /**
     * Test saving player's armor and buff
     */
    @Test
    public void testArmorAndBuff() {
        inventoryComponent.pickup(new Armor());
       // statsComponent.setDamageBuff(2.5f);
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(20, playerConfig.armour);

    }

    /**
     * Test svaing player;s damage buff
     */
    @Test
    public void testBuff() {
        inventoryComponent.pickup(new DamageBuff());
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(15.0f, playerConfig.buff, 0.01f);
    }

    /**
     * Test saving player's crit chance and crit status
     */

    @Test
    public void testCritStats() {

        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(5f, playerConfig.critChance, 0.01f);
        assertEquals(true, playerConfig.canCrit);
    }


    /**
     * Test saving player with pets
     */
    @Test
    public void testPets() {
        inventoryComponent.pickup(new Pet() {
            @Override
            public String getName() {
                return "pet";
            }

            @Override
            public Texture getIcon() {
                return null;
            }

            @Override
            protected String getPetSpecification() {
                return "dragon";
            }

            @Override
            protected Entity spawn(Entity entity) {
                return null;
            }
        });

        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(1, playerConfig.pets.length);
        assertEquals("pet:dragon", playerConfig.pets[0]);
    }

    /**
     * Test player with multiple pets
     */
    @Test
    public void testMultiplePets() {
        inventoryComponent.pickup(new Pet() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public Texture getIcon() {
                return null;
            }

            @Override
            public String getSpecification() {
                return "Dragon";
            }

            @Override
            protected String getPetSpecification() {
                return null;
            }

            @Override
            protected Entity spawn(Entity entity) {
                return null;
            }
        });
        inventoryComponent.pickup(new Pet() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public Texture getIcon() {
                return null;
            }

            @Override
            public String getSpecification() {
                return "Wolf";
            }

            @Override
            protected String getPetSpecification() {
                return null;
            }

            @Override
            protected Entity spawn(Entity entity) {
                return null;
            }
        });

        PlayerConfig playerConfig = generator.savePlayerState(player);
        String[] expectedPets = {"Dragon", "Wolf"};
        assertArrayEquals(expectedPets, playerConfig.pets);
    }

    /**
     * Test player with no coins
     */
    @Test
    public void testNoCoins() {
        coinsComponent.setCoins(0);
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(0, playerConfig.coins);
    }

    /**
     * Test saving player with no pets and items
     */
    @Test
    public void testEmptyInventoryAndPets() {
        PlayerConfig playerConfig = generator.savePlayerState(player);
        assertEquals(0, playerConfig.items.length);
        assertEquals(0, playerConfig.pets.length);
    }


}
