package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.NameComponent;
import com.csse3200.game.components.npc.NPCHealthBarComponent;
import com.csse3200.game.components.player.inventory.DummyDestroyedHandler;
import com.csse3200.game.components.player.inventory.usables.TrapComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public class DeployableItemFactory extends LoadedFactory {

    public Entity createTargetDummy() {

        Entity targetDummy  = new Entity()
                .addComponent(new NameComponent("Target Dummy"))
                .addComponent(new HitboxComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.PLAYER).setDensity(2000f))
                .addComponent(new CombatStatsComponent(100,0))
                .addComponent(new NPCHealthBarComponent())
                .addComponent(new DummyDestroyedHandler())
                .addComponent(new TextureRenderComponent(new Texture("images/items/target_dummy_deployed.png")))
                .addComponent(new PhysicsComponent());

        targetDummy.getComponent(TextureRenderComponent.class).scaleEntity();

        return targetDummy;
    }


    public Entity createBearTrap() {
        Entity bearTrap = new Entity()
                .addComponent(new HitboxComponent())
                .addComponent(new CombatStatsComponent(100,50))
                .addComponent(new TextureRenderComponent(new Texture("images/items/trap_open.png")))
                .addComponent(new PhysicsComponent())
                .addComponent(new TrapComponent());


        bearTrap.getComponent(TextureRenderComponent.class).scaleEntity();

        return bearTrap;
    }

    public Entity createRingFire() {
        Entity ringFire = new Entity()
                .addComponent(new HitboxComponent())
                .addComponent(new CombatStatsComponent(200,20))
                .addComponent(new TextureRenderComponent(new Texture("images/items/Fire.png")))
                .addComponent(new PhysicsComponent())
                .addComponent(new TrapComponent());

        ringFire.getComponent(TextureRenderComponent.class).scaleEntity();

        return ringFire;
    }

}
