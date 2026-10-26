package com.battleverse.game.combat;

import com.battleverse.game.player.Player;

public class DamageSystem {

    public static boolean applyAttack(
            Player attacker,
            Player target,
            Attack attack) {

        if (target.isInvulnerable()) {
            return false;
        }

        if (!Hitbox.isHit(
                attacker,
                target,
                attack)) {

            return false;
        }

        target.addDamage(
                attack.getDamage()
        );

        // -----------------------------------------------------
        // KNOCKBACK
        // -----------------------------------------------------

        float damageMultiplier =
                1f +
                Math.min(
                        target.getDamage(),
                        300f
                ) / 100f;

        // Más knockback cuanto menos vida quede
        float healthFactor =
                1f +
                (1f - target.getHealth() / Player.MAX_HEALTH)
                * 1.4f;

        float weight =
                target.getData().getWeight();

        float horizontal =
                attack.getKnockbackX()
                * damageMultiplier
                * healthFactor
                / weight
                * attacker.getFacing();

        float vertical =
                attack.getKnockbackY()
                * damageMultiplier
                * healthFactor
                / weight;

        horizontal =
                Math.max(
                        -1400f,
                        Math.min(
                                1400f,
                                horizontal
                        )
                );

        vertical =
                Math.max(
                        -200f,
                        Math.min(
                                900f,
                                vertical
                        )
                );

        target.knockback(
                horizontal,
                vertical
        );

        // -----------------------------------------------------
        // ENERGÍA
        // -----------------------------------------------------

        attacker.addSpecialEnergy(8f);
        target.addSpecialEnergy(4f);

        return true;
    }
}