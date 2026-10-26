package com.battleverse.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MainMenuScreen extends ScreenAdapter {

    private final Game game;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private int selectedButton = 0;

    // Mensaje temporal ("Próximamente...") cuando se elige una opción
    // que todavía no está implementada.
    private String toast = null;
    private float toastTimer = 0f;

    private final String[] buttons = {
        "ARCADE",
        "MODO CARRERA",
        "VERSUS",
        "OPCIONES"
    };

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {

        if (toastTimer > 0f) {
            toastTimer -= delta;

            if (toastTimer <= 0f) {
                toast = null;
            }
        }

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // -------------------------
        // FONDO
        // -------------------------

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.03f, 0.03f, 0.06f, 1f);
        shapeRenderer.rect(0, 0, width, height);

        // -------------------------
        // BOTONES
        // -------------------------

        float buttonWidth = 400;
        float buttonHeight = 65;
        float buttonX = (width - buttonWidth) / 2f;

        for (int i = 0; i < buttons.length; i++) {

            float buttonY = height - 220 - (i * 90);

            if (i == selectedButton) {
                shapeRenderer.setColor(0.9f, 0.1f, 0.1f, 1f);
            } else {
                shapeRenderer.setColor(0.15f, 0.15f, 0.20f, 1f);
            }

            shapeRenderer.rect(
                buttonX,
                buttonY,
                buttonWidth,
                buttonHeight
            );
        }

        shapeRenderer.end();

        // -------------------------
        // TEXTO
        // -------------------------

        batch.begin();

        font.getData().setScale(3f);
        font.setColor(Color.WHITE);

        String title = "BATTLEVERSE";

        layout.setText(font, title);

        font.draw(
            batch,
            title,
            width / 2f - layout.width / 2f,
            height - 80
        );

        font.getData().setScale(1.5f);

        for (int i = 0; i < buttons.length; i++) {

            float buttonY = height - 180 - (i * 90);

            font.setColor(Color.WHITE);

            font.draw(
                batch,
                buttons[i],
                buttonX + 100,
                buttonY
            );
        }

        if (toast != null) {

            font.getData().setScale(1.2f);
            font.setColor(1f, 0.85f, 0.3f, 1f);

            layout.setText(font, toast);

            font.draw(
                batch,
                toast,
                width / 2f - layout.width / 2f,
                110f
            );
        }

        batch.end();

        handleInput();
    }

    private void handleInput() {

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {

            selectedButton++;

            if (selectedButton >= buttons.length) {
                selectedButton = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {

            selectedButton--;

            if (selectedButton < 0) {
                selectedButton = buttons.length - 1;
            }
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {

            selectButton();
        }
    }

    private void selectButton() {

        switch (selectedButton) {

            case 0:
                game.setScreen(new CharacterSelectScreen(game));
                break;

            case 1:
                showToast("MODO CARRERA: próximamente");
                break;

            case 2:
                showToast("VERSUS: próximamente");
                break;

            case 3:
                showToast("OPCIONES: próximamente");
                break;
        }
    }

    private void showToast(String message) {
        toast = message;
        toastTimer = 1.6f;
    }

    @Override
    public void dispose() {

        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}