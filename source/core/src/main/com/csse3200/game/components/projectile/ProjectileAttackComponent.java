package com.csse3200.game.components.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.BodyUserData;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This component acts out a projectile life cycle.
 * It's built, shot, waits for collision, attempts to attack, then it is removed.
 * requires
 *  ProjectileActions - Allows the projectile to be shot.
 *  CombatStatsComponent - Used to hit() on collision.
 *  HitboxComponent - triggers event on collisions.
 */
public class ProjectileAttackComponent extends Component {
    private static final Logger logger = LoggerFactory.getLogger(ProjectileAttackComponent.class);

    private final short targetLayer;
    private CombatStatsComponent combatStats;
    private HitboxComponent hitboxComponent;
    private Vector2 speed;
    private final Vector2 direction;
    private final Vector2 parentPosition;
    private final float range;

    /**
     *  Sets up vars for a projectile attack.
     *  @param layer physics layer that the projectile shot on. Provided by ProjectileConfig.
     *  @param direction Direction being shot. Example - Vector2Utils. LEFT shoots left.
     *  @param speed Set in the projectileConfig. Example - Vector2(3,3) is 3m\s etc.
    */
    public ProjectileAttackComponent(short layer, Vector2 direction, Vector2 speed, Vector2 parentPosition, float range) {
        this.targetLayer = layer;
        this.speed = speed;
        this.direction = direction;
        this.parentPosition = parentPosition;
        this.range = range;
    }

    public void setSpeed(Vector2 v) {
        speed = v;
    }

    public Vector2 getSpeed() {
        return speed;
    }

    /**
     * On create a collision listener is started and the ProjectileAction shoot is started.
     * The collisionStart event is triggered by PhysicsContactListener, based on HitboxComponent.
     */
    @Override
    public void create() {
        combatStats = entity.getComponent(CombatStatsComponent.class);
        hitboxComponent = entity.getComponent(HitboxComponent.class);
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
        entity.getComponent(ProjectileActions.class).shoot(direction, speed, parentPosition, range);
    }

    /**
     * Method is called by the Event listener in Create(), args are provided by PhysicsContactListener.
     * @param me Fixture to test if belongs to this entity.
     * @param other Fixture belonging to the hit entity.
     */
    private void onCollisionStart(Fixture me, Fixture other) {
        if (hitboxComponent.getFixture() != me) {
            return; // Not our hit-box.
        }

        if (other.getFilterData().categoryBits == PhysicsLayer.WEAPON) {
            return; // the other is a weapon.
        }

        if (!PhysicsLayer.contains(targetLayer, other.getFilterData().categoryBits)) {
            if (!PhysicsLayer.contains(PhysicsLayer.OBSTACLE, other.getFilterData().categoryBits)) {
                return; // other is on the wrong layer.
            }
        }

        if (!(other.getBody().getUserData() instanceof BodyUserData data)) {
            logger.error("user data incorrect");
            return;
        }
        Entity target = data.entity;
        CombatStatsComponent targetStats = target.getComponent(CombatStatsComponent.class);

        if (targetStats != null) {
            targetStats.hit(combatStats);
        }

        //I have changed disposal... im keeping prev here to show off a bit of danger.
        //ServiceLocator.getGameAreaService().getGameArea().disposeEntity(entity);
        ServiceLocator.getEntityService().markEntityForRemoval(entity);
    }
}