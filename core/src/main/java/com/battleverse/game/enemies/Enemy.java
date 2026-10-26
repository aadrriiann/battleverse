package com.battleverse.game.enemies;

import com.battleverse.game.player.CharacterData;
import com.battleverse.game.player.Player;

public class Enemy extends Player {

    public Enemy(
            float x,
            float y,
            CharacterData data) {

        super(x, y, data);
    }
}