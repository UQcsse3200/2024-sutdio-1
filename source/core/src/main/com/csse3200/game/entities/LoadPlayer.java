package com.csse3200.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.player.*;
import com.csse3200.game.components.player.inventory.*;
import com.csse3200.game.entities.configs.PlayerConfig;
import com.csse3200.game.entities.factories.*;
import com.csse3200.game.entities.factories.AnimationFactory;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

import java.util.Objects;


public class LoadPlayer extends LoadedFactory {

    private final WeaponFactory weaponFactory;
    private final CollectibleFactory collectibleFactory;
    private final ItemFactory itemFactory;
    private final AnimationFactory animationFactory;
    private final InventoryComponent inventoryComponent;

    /**
     * Construct a new Player Factory (and load all of its assets)
     */
    public LoadPlayer() {

        this.weaponFactory = new WeaponFactory();
        this.collectibleFactory = new CollectibleFactory();
        this.animationFactory = new AnimationFactory();
        this.itemFactory = new ItemFactory();
        this.inventoryComponent = new InventoryComponent();
    }

    /**
     * Create a player entity
     *
     * @param config the config for the player.
     * @return entity
     */
    public Entity createPlayer(PlayerConfig config) {
        Entity player = new Entity();
        addComponents(config);
        addWeaponsAndItems(player, config);
        addAtlas(player, config);
        PhysicsUtils.setScaledCollider(player, 0.6f, 0.3f);
        player.getComponent(ColliderComponent.class).setDensity(1.5f);
        return player;
    }
    public Entity addAtlas(Entity player, PlayerConfig config) {
        TextureAtlas atlas = new TextureAtlas(config.textureAtlasFilename);
        TextureRegion defaultTexture = atlas.findRegion("idle");
        player.setScale(1f, (float) defaultTexture.getRegionHeight() / defaultTexture.getRegionWidth());
        return player;
    }

    public Entity addComponents(PlayerConfig config) {
        Entity player = new Entity()
                .addComponent(new PlayerConfigComponent(config))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYER))
                .addComponent(new PlayerActions())
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack, true))
                .addComponent(inventoryComponent)
                .addComponent(new ItemPickupComponent())
                //.addComponent(ServiceLocator.getInputService().getInputFactory().createForPlayer())
                .addComponent(new PlayerStatsDisplay())
                .addComponent(animationFactory.createAnimationComponent(config.textureAtlasFilename))
                .addComponent(new PlayerAnimationController())
                .addComponent(new PlayerInventoryDisplay(inventoryComponent))
                .addComponent(new PlayerHealthDisplay())
                .addComponent(new WeaponComponent(
                        new Sprite(new Texture("images/Weapons/knife.png")),
                        Collectible.Type.RANGED_WEAPON,
                        10, 1, 1, 10, 10, 0));

        return player;
    }

    public void createRanged(PlayerConfig config, Entity player) {

        Collectible melee = weaponFactory.create(Collectible.Type.MELEE_WEAPON, config.melee);
        if (melee instanceof MeleeWeapon meleeWeapon) {
            WeaponComponent meleeWeaponComponent = new WeaponComponent(
                    new Sprite(meleeWeapon.getIcon()),  // Use texture from the melee weapon class
                    Collectible.Type.MELEE_WEAPON,
                    meleeWeapon.getDamage(),
                    meleeWeapon.getRange(),
                    meleeWeapon.getFireRate(),
                    0, 0, 0
            );
            inventoryComponent.getInventory().setMelee(meleeWeapon); // Set melee weapon in the inventory
            player.addComponent(meleeWeaponComponent);
        }
    }

    public void createMelee(PlayerConfig config, Entity player) {

        Collectible ranged = weaponFactory.create(Collectible.Type.RANGED_WEAPON, config.melee);
        if (ranged instanceof RangedWeapon rangedWeapon) {
            WeaponComponent rangedWeaponComponent = new WeaponComponent(
                    new Sprite(rangedWeapon.getIcon()),
                    Collectible.Type.RANGED_WEAPON,
                    rangedWeapon.getDamage(),
                    rangedWeapon.getRange(),
                    rangedWeapon.getFireRate(),
                    rangedWeapon.getAmmo(),
                    rangedWeapon.getMaxAmmo(),
                    rangedWeapon.getReloadTime()
            );
            player.addComponent(rangedWeaponComponent); // Add melee weapon component to the player
            inventoryComponent.getInventory().setRanged(rangedWeapon); // Set melee weapon in the inventory
        }
    }

    private void addWeaponsAndItems(Entity player, PlayerConfig config) {
        if (!Objects.equals(config.melee, "")) {
            createMelee(config, player);
        }

        if (!Objects.equals(config.ranged, "")) {
            createRanged(config, player);
        }

        if (config.items != null) {
            for (String item : config.items) {
                itemFactory.create(item);
            }
        }
    }


}
