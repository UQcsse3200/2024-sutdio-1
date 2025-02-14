package com.csse3200.game.entities.configs;

/**
 * Defines the configuration for NPC attacks.
 */
public class AttackConfig {
    public MeleeAttack melee = null;
    public RangeAttack ranged = null;
    public AOEAttack aoe = null;

    /**
     * Configuration for a melee attack.
     */
    public static class MeleeAttack {
        public float range;
        public float rate;
        public EffectConfig[] effects = new EffectConfig[0];
    }

    /**
     * Configuration for a ranged attack.
     */
    public static class RangeAttack {
        public float range;
        public float rate;
        public int type;
        public EffectConfig[] effects = new EffectConfig[0];
    }

    /**
     * Configuration for an AOE attack.
     */
    public static class AOEAttack {
        public float range;
        public float rate;
        public EffectConfig[] effects = new EffectConfig[0];
    }
}
