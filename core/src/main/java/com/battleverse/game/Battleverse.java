package com.battleverse.game;

import com.badlogic.gdx.Game;
import com.battleverse.game.screens.MainMenuScreen;

public class Battleverse extends Game {

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}