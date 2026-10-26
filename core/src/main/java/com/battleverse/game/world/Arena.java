package com.battleverse.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.battleverse.game.player.Player;

public class Arena {

    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    private final float leftBound = 80f;
    private final float rightBound = 1200f;

    private final float mainPlatformX = 80f;
    private final float mainPlatformY = 70f;
    private final float mainPlatformWidth = 1120f;
    private final float mainPlatformHeight = 35f;

    public void resolvePlayer(
            Player player,
            float previousY) {

        float playerBottom =
                player.getY();

        float previousBottom =
                previousY;

        // -----------------------------------------------------
        // PLATAFORMA PRINCIPAL
        // -----------------------------------------------------

        if (player.getVelocityY() <= 0f) {

            if (previousBottom >=
                    mainPlatformY + mainPlatformHeight

                    && playerBottom <=
                    mainPlatformY + mainPlatformHeight

                    && player.getX() +
                    player.getWidth() >
                    mainPlatformX

                    && player.getX() <
                    mainPlatformX +
                    mainPlatformWidth) {

                player.setGrounded(true);

                // Aquí se coloca el jugador encima.
                setPlayerY(
                        player,
                        mainPlatformY +
                        mainPlatformHeight
                );
            }

            // -------------------------------------------------
            // PLATAFORMA 2
            // -------------------------------------------------

            resolvePlatform(
                    player,
                    previousY,
                    180f,
                    270f,
                    300f,
                    25f
            );

            // -------------------------------------------------
            // PLATAFORMA 3
            // -------------------------------------------------

            resolvePlatform(
                    player,
                    previousY,
                    790f,
                    350f,
                    300f,
                    25f
            );

            // -------------------------------------------------
            // PLATAFORMA 4
            // -------------------------------------------------

            resolvePlatform(
                    player,
                    previousY,
                    500f,
                    190f,
                    280f,
                    22f
            );
        }
    }

    private void resolvePlatform(
            Player player,
            float previousY,
            float x,
            float y,
            float width,
            float height) {

        if (player.getVelocityY() > 0f) {
            return;
        }

        float platformTop =
                y + height;

        if (previousY >= platformTop

                && player.getY() <= platformTop

                && player.getX() +
                player.getWidth() > x

                && player.getX() <
                x + width) {

            setPlayerY(
                    player,
                    platformTop
            );

            player.setGrounded(true);
        }
    }

    private void setPlayerY(Player player, float y) {
        player.setY(y);
    }

    public void draw(
            ShapeRenderer shapeRenderer) {

        shapeRenderer.setColor(
                new Color(
                        0.08f,
                        0.08f,
                        0.12f,
                        1f
                )
        );

        shapeRenderer.rect(
                mainPlatformX,
                mainPlatformY,
                mainPlatformWidth,
                mainPlatformHeight
        );

        shapeRenderer.setColor(
                new Color(
                        0.18f,
                        0.18f,
                        0.25f,
                        1f
                )
        );

        shapeRenderer.rect(
                180f,
                270f,
                300f,
                25f
        );

        shapeRenderer.rect(
                790f,
                350f,
                300f,
                25f
        );

        shapeRenderer.rect(
                500f,
                190f,
                280f,
                22f
        );

        // Filo superior más claro en cada plataforma para dar
        // sensación de profundidad/grosor.
        shapeRenderer.setColor(
                new Color(0.32f, 0.32f, 0.42f, 1f)
        );

        shapeRenderer.rect(mainPlatformX, mainPlatformY + mainPlatformHeight - 3f, mainPlatformWidth, 3f);
        shapeRenderer.rect(180f, 270f + 25f - 3f, 300f, 3f);
        shapeRenderer.rect(790f, 350f + 25f - 3f, 300f, 3f);
        shapeRenderer.rect(500f, 190f + 22f - 3f, 280f, 3f);
    }

    public float getSpawnY() {

        return mainPlatformY +
                mainPlatformHeight;
    }

    public float getLeftBound() {

        return leftBound;
    }

    public float getRightBound() {

        return rightBound;
    }
}