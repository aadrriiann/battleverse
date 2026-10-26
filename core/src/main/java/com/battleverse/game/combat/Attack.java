package com.battleverse.game.combat;

public class Attack {

    private final String name;
    private final float damage;
    private final float knockbackX;
    private final float knockbackY;
    private final float range;
    private final float verticalRange;
    private final float duration;
    private final float cooldown;
    private final float energyCost;
    private final boolean special;

    public Attack(
            String name,
            float damage,
            float knockbackX,
            float knockbackY,
            float range,
            float verticalRange,
            float duration,
            float cooldown,
            float energyCost,
            boolean special) {

        this.name = name;
        this.damage = damage;
        this.knockbackX = knockbackX;
        this.knockbackY = knockbackY;
        this.range = range;
        this.verticalRange = verticalRange;
        this.duration = duration;
        this.cooldown = cooldown;
        this.energyCost = energyCost;
        this.special = special;
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }

    public float getKnockbackX() {
        return knockbackX;
    }

    public float getKnockbackY() {
        return knockbackY;
    }

    public float getRange() {
        return range;
    }

    public float getVerticalRange() {
        return verticalRange;
    }

    public float getDuration() {
        return duration;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getEnergyCost() {
        return energyCost;
    }

    public boolean isSpecial() {
        return special;
    }
}