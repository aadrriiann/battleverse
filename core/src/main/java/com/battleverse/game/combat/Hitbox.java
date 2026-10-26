package com.battleverse.game.combat;

import com.battleverse.game.player.Player;

public class Hitbox {

    public static boolean isHit(
            Player attacker,
            Player target,
            Attack attack) {

        float attackerCenterX =
                attacker.getX() +
                attacker.getWidth() / 2f;

        float targetCenterX =
                target.getX() +
                target.getWidth() / 2f;

        float attackerCenterY =
                attacker.getY() +
                attacker.getHeight() / 2f;

        float targetCenterY =
                target.getY() +
                target.getHeight() / 2f;

        float horizontalDistance =
                targetCenterX -
                attackerCenterX;

        float verticalDistance =
                Math.abs(
                        targetCenterY -
                        attackerCenterY
                );

        // El enemigo tiene que estar delante.
        if (horizontalDistance *
                attacker.getFacing()
                < -attacker.getWidth() / 2f) {

            return false;
        }

        return Math.abs(horizontalDistance)
                <= attack.getRange()

                && verticalDistance
                <= attack.getVerticalRange();
    }
}