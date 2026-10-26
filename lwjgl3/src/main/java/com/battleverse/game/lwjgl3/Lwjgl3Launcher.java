package com.battleverse.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.battleverse.game.Battleverse;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(
            new Battleverse(),
            getDefaultConfiguration()
        );
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {

        Lwjgl3ApplicationConfiguration configuration =
                new Lwjgl3ApplicationConfiguration();

        // Nombre de la ventana
        configuration.setTitle("Battleverse");

        // VSync
        configuration.useVsync(true);

        // 60 FPS
        configuration.setForegroundFPS(60);

        // Tamaño inicial de la ventana
        configuration.setWindowedMode(1280, 720);

        // La ventana empieza maximizada
        configuration.setMaximized(true);

        return configuration;
    }
}