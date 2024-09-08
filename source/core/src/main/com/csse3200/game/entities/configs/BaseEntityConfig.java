package com.csse3200.game.entities.configs;

/**
 * Defines a basic set of properties stored in entities config files to be loaded by Entity Factories.
 */
public class BaseEntityConfig {
    public int health = 1;
    public int baseAttack = 0;
    public float wanderSpeed = 1f;
    public float chaseSpeed = 1.5f;
    public float viewDistance = 5f;
    public float chaseDistance = 5f;
}