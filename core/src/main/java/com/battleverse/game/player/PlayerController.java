package com.battleverse.game.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;

public class PlayerController {

    private final Player player;

    public PlayerController(Player player) {

        this.player = player;
    }

    public void update() {

        if (Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            player.moveLeft();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) ||
                Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            player.moveRight();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

            player.jump();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {

            player.attack(
                    player.getData().getNormalAttack()
            );
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {

            player.attack(
                    player.getData().getHeavyAttack()
            );
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {

            player.attack(
                    player.getData().getSpecialAttack()
            );
        }
    }
}