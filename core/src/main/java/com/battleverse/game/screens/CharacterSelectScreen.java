package com.battleverse.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.battleverse.game.player.CharacterData;

public class CharacterSelectScreen extends ScreenAdapter {

    private static final float WIDTH = 1280f;
    private static final float HEIGHT = 720f;

    private final Game game;

    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    private final CharacterData[] characters;

    private int selected = 0;

    public CharacterSelectScreen(Game game) {

        this.game = game;

        camera =
                new OrthographicCamera();

        viewport =
                new FitViewport(
                        WIDTH,
                        HEIGHT,
                        camera
                );

        batch =
                new SpriteBatch();

        shapeRenderer =
                new ShapeRenderer();

        font =
                new BitmapFont();

        characters =
                CharacterData.getAllCharacters();

        camera.position.set(
                WIDTH / 2f,
                HEIGHT / 2f,
                0
        );

        camera.update();
    }

    @Override
    public void render(float delta) {

        update();
        draw();
    }

    private void update() {

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.LEFT)) {

            selected--;

            if (selected < 0) {
                selected =
                        characters.length - 1;
            }
        }

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.RIGHT)) {

            selected++;

            if (selected >= characters.length) {
                selected = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.ENTER)) {

            game.setScreen(
                    new GameScreen(
                            game,
                            characters[selected]
                    )
            );
        }

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.ESCAPE)) {

            game.setScreen(
                    new MainMenuScreen(game)
            );
        }
    }

    private void draw() {

        Gdx.gl.glClearColor(
                0.025f,
                0.03f,
                0.06f,
                1f
        );

        Gdx.gl.glClear(
                GL20.GL_COLOR_BUFFER_BIT
        );

        viewport.apply();

        shapeRenderer.setProjectionMatrix(
                camera.combined
        );

        batch.setProjectionMatrix(
                camera.combined
        );

        CharacterData character =
                characters[selected];

        // =========================
        // FONDO
        // =========================

        shapeRenderer.begin(
                ShapeRenderer.ShapeType.Filled
        );

        shapeRenderer.setColor(
                0.025f,
                0.03f,
                0.06f,
                1f
        );

        shapeRenderer.rect(
                0,
                0,
                WIDTH,
                HEIGHT
        );

        // =========================
        // TARJETA
        // =========================

        shapeRenderer.setColor(
                0.08f,
                0.09f,
                0.14f,
                1f
        );

        shapeRenderer.rect(
                330,
                110,
                620,
                500
        );

        // Personaje
        shapeRenderer.setColor(
                character.getColor()
        );

        shapeRenderer.rect(
                585,
                275,
                character.getWidth(),
                character.getHeight()
        );

        shapeRenderer.circle(
                615,
                340,
                25
        );

        shapeRenderer.end();

        // =========================
        // TEXTO
        // =========================

        batch.begin();

        font.setColor(
                Color.WHITE
        );

        font.getData().setScale(
                2.2f
        );

        font.draw(
                batch,
                "ELIGE TU LUCHADOR",
                430,
                660
        );

        font.setColor(
                character.getColor()
        );

        font.getData().setScale(
                2.5f
        );

        font.draw(
                batch,
                character.getName(),
                550,
                245
        );

        font.setColor(
                Color.WHITE
        );

        font.getData().setScale(
                1f
        );

        font.draw(
                batch,
                "VELOCIDAD",
                430,
                190
        );

        font.draw(
                batch,
                "SALTO",
                430,
                165
        );

        font.draw(
                batch,
                "PESO",
                430,
                140
        );

        font.draw(
                batch,
                "← / →  CAMBIAR",
                760,
                190
        );

        font.draw(
                batch,
                "ENTER  ELEGIR",
                760,
                165
        );

        font.draw(
                batch,
                "ESC  VOLVER",
                760,
                140
        );

        batch.end();

        // =========================
        // BARRAS
        // =========================

        drawStats(character);
    }

    private void drawStats(
            CharacterData character) {

        shapeRenderer.begin(
                ShapeRenderer.ShapeType.Filled
        );

        // Velocidad
        drawStatBar(
                540,
                182,
                Math.min(
                        character.getSpeed()
                                / 600f,
                        1f
                ),
                character.getColor()
        );

        // Salto
        drawStatBar(
                540,
                157,
                Math.min(
                        character.getJumpForce()
                                / 1000f,
                        1f
                ),
                character.getColor()
        );

        // Peso
        drawStatBar(
                540,
                132,
                Math.min(
                        character.getWeight()
                                / 1.5f,
                        1f
                ),
                character.getColor()
        );

        shapeRenderer.end();
    }

    private void drawStatBar(
            float x,
            float y,
            float value,
            Color color) {

        shapeRenderer.setColor(
                0.15f,
                0.15f,
                0.20f,
                1f
        );

        shapeRenderer.rect(
                x,
                y,
                180,
                10
        );

        shapeRenderer.setColor(
                color
        );

        shapeRenderer.rect(
                x,
                y,
                180 * value,
                10
        );
    }

    @Override
    public void resize(
            int width,
            int height) {

        viewport.update(
                width,
                height,
                true
        );
    }

    @Override
    public void dispose() {

        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}