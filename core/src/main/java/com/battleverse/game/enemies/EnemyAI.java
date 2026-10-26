package com.battleverse.game.enemies;

import com.battleverse.game.player.Player;

public class EnemyAI {

    private final Enemy enemy;
    private final Player target;

    private float decisionTimer = 0f;

    public EnemyAI(
            Enemy enemy,
            Player target) {

        this.enemy = enemy;
        this.target = target;
    }

    public void update(float delta) {

        decisionTimer -= delta;

        float enemyCenter =
                enemy.getX() +
                enemy.getWidth() / 2f;

        float playerCenter =
                target.getX() +
                target.getWidth() / 2f;

        float distance =
                playerCenter - enemyCenter;

        // -----------------------------------------------------
        // MOVIMIENTO
        // -----------------------------------------------------

        if (distance > 100f) {

            enemy.moveRight();

        } else if (distance < -100f) {

            enemy.moveLeft();
        }

        // -----------------------------------------------------
        // SALTO
        // -----------------------------------------------------

        if (Math.abs(
                target.getY() - enemy.getY()
        ) > 90f) {

            if (enemy.isGrounded()) {

                enemy.jump();
            }
        }

        // -----------------------------------------------------
        // ATAQUES
        // -----------------------------------------------------

        if (decisionTimer <= 0f) {

            decisionTimer = 0.12f;

            if (Math.abs(distance) < 160f) {

                float random =
                        (float) Math.random();

                if (random < 0.15f) {

                    enemy.attack(
                            enemy.getData()
                                    .getSpecialAttack()
                    );

                } else if (random < 0.50f) {

                    enemy.attack(
                            enemy.getData()
                                    .getHeavyAttack()
                    );

                } else {

                    enemy.attack(
                            enemy.getData()
                                    .getNormalAttack()
                    );
                }
            }
        }
    }
}