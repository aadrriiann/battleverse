package com.battleverse.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.battleverse.game.combat.DamageSystem;
import com.battleverse.game.combat.SpecialVfx;
import com.battleverse.game.enemies.Enemy;
import com.battleverse.game.enemies.EnemyAI;
import com.battleverse.game.player.CharacterData;
import com.battleverse.game.player.Player;
import com.battleverse.game.player.PlayerController;
import com.battleverse.game.world.Arena;

public class GameScreen implements com.badlogic.gdx.Screen {

    private final Game game;

    private final CharacterData playerData;

    private Player player;
    private Enemy enemy;

    private PlayerController controller;
    private EnemyAI enemyAI;

    private Arena arena;

    private OrthographicCamera camera;
    private Viewport viewport;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private Texture pixel;

    private float hitStopTimer = 0f;
    private float shakeTimer = 0f;
    private float hudPulseTimer = 0f;

    private boolean gameOver = false;
    private boolean victory = false;

    private float rulesTimer = 3.5f;
    private boolean rulesVisible = true;

    private final SpecialVfx specialVfx = new SpecialVfx();
    private boolean playerSpecialStarted = false;
    private boolean enemySpecialStarted = false;

    public GameScreen(
            Game game,
            CharacterData playerData) {

        this.game = game;
        this.playerData = playerData;

        camera =
                new OrthographicCamera();

        viewport =
                new FitViewport(
                        Arena.WORLD_WIDTH,
                        Arena.WORLD_HEIGHT,
                        camera
                );

        batch =
                new SpriteBatch();

        shapeRenderer =
                new ShapeRenderer();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        pixel =
                new Texture(
                        Gdx.files.internal("libgdx.png")
                );

        arena =
                new Arena();

        player =
                new Player(
                        250f,
                        arena.getSpawnY(),
                        playerData
                );

        enemy =
                new Enemy(
                        900f,
                        arena.getSpawnY(),
                        CharacterData.randomOpponent(
                                playerData.getName()
                        )
                );

        controller =
                new PlayerController(player);

        enemyAI =
                new EnemyAI(
                        enemy,
                        player
                );
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        hudPulseTimer += delta;

        handleGlobalInput();

        if (gameOver) {

            draw();

            return;
        }

        if (rulesVisible) {

            rulesTimer -= delta;

            if (rulesTimer <= 0f
                    || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                    || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                rulesVisible = false;
                rulesTimer = 0f;
            }

            draw();
            drawRulesOverlay();

            return;
        }

        // -----------------------------------------------------
        // HIT STOP
        // -----------------------------------------------------

        if (hitStopTimer > 0f) {

            hitStopTimer -= delta;

            draw();

            return;
        }

        update(delta);

        draw();
    }

    private void handleGlobalInput() {

        if (Gdx.input.isKeyJustPressed(
                Input.Keys.ESCAPE)) {

            game.setScreen(
                    new MainMenuScreen(game)
            );
        }

        if (gameOver &&
                Gdx.input.isKeyJustPressed(
                        Input.Keys.ENTER)) {

            game.setScreen(
                    new CharacterSelectScreen(game)
            );
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================

    private void update(float delta) {

        controller.update();

        enemyAI.update(delta);

        player.update(
                delta,
                arena
        );

        enemy.update(
                delta,
                arena
        );

        // Especiales: arrancar VFX al iniciar
        if (player.isSpecialAttacking()) {
            if (!playerSpecialStarted) {
                specialVfx.startSpecial(player);
                playerSpecialStarted = true;
                shakeTimer = Math.max(shakeTimer, 0.2f);
            }
        } else {
            playerSpecialStarted = false;
        }

        if (enemy.isSpecialAttacking()) {
            if (!enemySpecialStarted) {
                specialVfx.startSpecial(enemy);
                enemySpecialStarted = true;
                shakeTimer = Math.max(shakeTimer, 0.2f);
            }
        } else {
            enemySpecialStarted = false;
        }

        specialVfx.update(delta, player, enemy);

        // -----------------------------------------------------
        // ATAQUE DEL JUGADOR
        // -----------------------------------------------------

        if (player.isAttacking()
                && !player.hasAttackHit()) {

            if (DamageSystem.applyAttack(
                    player,
                    enemy,
                    player.getCurrentAttack()
            )) {

                player.setAttackHit(true);

                boolean special =
                        player.getCurrentAttack() != null
                                && player.getCurrentAttack().isSpecial();

                if (special) {
                    specialVfx.onSpecialHit(player, enemy);
                    hitStopTimer = 0.14f;
                    shakeTimer = 0.35f;
                } else {
                    hitStopTimer = 0.055f;
                    shakeTimer = 0.12f;
                }

                if (enemy.isHealthDepleted()) {
                    handleStockLoss(enemy, true);
                }
            }
        }

        // -----------------------------------------------------
        // ATAQUE DEL ENEMIGO
        // -----------------------------------------------------

        if (enemy.isAttacking()
                && !enemy.hasAttackHit()) {

            if (DamageSystem.applyAttack(
                    enemy,
                    player,
                    enemy.getCurrentAttack()
            )) {

                enemy.setAttackHit(true);

                boolean special =
                        enemy.getCurrentAttack() != null
                                && enemy.getCurrentAttack().isSpecial();

                if (special) {
                    specialVfx.onSpecialHit(enemy, player);
                    hitStopTimer = 0.14f;
                    shakeTimer = 0.35f;
                } else {
                    hitStopTimer = 0.055f;
                    shakeTimer = 0.12f;
                }

                if (player.isHealthDepleted()) {
                    handleStockLoss(player, false);
                }
            }
        }

        // -----------------------------------------------------
        // CAÍDAS / BLAST ZONES (pierdes una vida)
        // -----------------------------------------------------

        if (!gameOver && isOutOfBounds(player)) {
            handleStockLoss(player, false);
        }

        if (!gameOver && isOutOfBounds(enemy)) {
            handleStockLoss(enemy, true);
        }

        // -----------------------------------------------------
        // SHAKE
        // -----------------------------------------------------

        if (shakeTimer > 0f) {

            shakeTimer -= delta;

            camera.position.x =
                    Arena.WORLD_WIDTH / 2f
                    + ((float)Math.random() - 0.5f)
                    * 10f;

            camera.position.y =
                    Arena.WORLD_HEIGHT / 2f
                    + ((float)Math.random() - 0.5f)
                    * 10f;

        } else {

            camera.position.x =
                    Arena.WORLD_WIDTH / 2f;

            camera.position.y =
                    Arena.WORLD_HEIGHT / 2f;
        }

        camera.update();
    }

    private boolean isOutOfBounds(Player fighter) {

        return fighter.getY() < -120f
                || fighter.getY() > Arena.WORLD_HEIGHT + 180f
                || fighter.getX() + fighter.getWidth() < -80f
                || fighter.getX() > Arena.WORLD_WIDTH + 80f;
    }

    private void handleStockLoss(Player fighter, boolean playerScored) {

        fighter.loseLife();

        if (fighter.getLives() <= 0) {

            gameOver = true;
            victory = playerScored;
            return;
        }

        if (fighter == player) {

            fighter.resetPosition(
                    250f,
                    arena.getSpawnY()
            );

        } else {

            fighter.resetPosition(
                    900f,
                    arena.getSpawnY()
            );
        }
    }

    // =========================================================
    // DRAW
    // =========================================================

    private void draw() {

        Gdx.gl.glClearColor(
                0.015f,
                0.015f,
                0.025f,
                1f
        );

        Gdx.gl.glClear(
                GL20.GL_COLOR_BUFFER_BIT
        );

        viewport.apply();

        drawBackground();

        shapeRenderer.setProjectionMatrix(
                camera.combined
        );

        shapeRenderer.begin(
                ShapeRenderer.ShapeType.Filled
        );

        arena.draw(shapeRenderer);

        shapeRenderer.end();

        batch.setProjectionMatrix(
                camera.combined
        );

        batch.begin();

        // -----------------------------------------------------
        // PERSONAJES
        // -----------------------------------------------------

        player.draw(batch);

        enemy.draw(batch);

        batch.end();

        // Efectos de especiales encima de los personajes
        shapeRenderer.setProjectionMatrix(camera.combined);
        specialVfx.draw(shapeRenderer);

        drawHUD();

        if (gameOver) {

            drawGameOver();
        }
    }

    // =========================================================
    // FONDO
    // =========================================================

    private void drawBackground() {

        shapeRenderer.setProjectionMatrix(
                camera.combined
        );

        shapeRenderer.begin(
                ShapeRenderer.ShapeType.Filled
        );

        shapeRenderer.setColor(
                0.02f,
                0.02f,
                0.05f,
                1f
        );

        shapeRenderer.rect(
                0f,
                0f,
                Arena.WORLD_WIDTH,
                Arena.WORLD_HEIGHT
        );

        shapeRenderer.setColor(
                0.15f,
                0.15f,
                0.25f,
                1f
        );

        shapeRenderer.circle(
                1050f,
                570f,
                65f
        );

        shapeRenderer.setColor(
                0.7f,
                0.7f,
                0.8f,
                1f
        );

        // estrellas

        for (int i = 0; i < 35; i++) {

            float x =
                    (i * 347) % 1200 + 40;

            float y =
                    400 + ((i * 173) % 250);

            shapeRenderer.circle(
                    x,
                    y,
                    2f
            );
        }

        shapeRenderer.end();
    }

    // =========================================================
    // HUD
    // =========================================================

    private void drawHUD() {

        // Barras con marco, degradado de color por % de vida
        // y aviso pulsante cuando la energía especial está lista.

        shapeRenderer.setProjectionMatrix(
                camera.combined
        );

        shapeRenderer.begin(
                ShapeRenderer.ShapeType.Filled
        );

        float playerHealthPct =
                player.getHealth() / player.getMaxHealth();

        float enemyHealthPct =
                enemy.getHealth() / enemy.getMaxHealth();

        drawBar(40f, 650f, 400f, 20f, playerHealthPct, healthColor(playerHealthPct));
        drawBar(840f, 650f, 400f, 20f, enemyHealthPct, healthColor(enemyHealthPct));

        float playerEnergyPct = player.getSpecialEnergy() / 100f;
        float enemyEnergyPct = enemy.getSpecialEnergy() / 100f;

        drawBar(40f, 620f, 250f, 10f, playerEnergyPct, new Color(0.1f, 0.8f, 1f, 1f));
        drawBar(990f, 620f, 250f, 10f, enemyEnergyPct, new Color(0.7f, 0.2f, 1f, 1f));

        shapeRenderer.end();

        // Aviso "especial listo": marco brillante pulsante alrededor
        // de la barra de energía cuando llega al 100%.
        if (playerEnergyPct >= 1f) {
            drawReadyGlow(40f, 620f, 250f, 10f, new Color(0.6f, 0.95f, 1f, 1f));
        }

        if (enemyEnergyPct >= 1f) {
            drawReadyGlow(990f, 620f, 250f, 10f, new Color(0.9f, 0.7f, 1f, 1f));
        }

        // -----------------------------------------------------
        // NOMBRES Y VIDAS
        // -----------------------------------------------------

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.getData().setScale(1.15f);
        font.setColor(Color.WHITE);
        font.draw(batch, player.getData().getName(), 40f, 685f);

        layout.setText(font, enemy.getData().getName());
        font.draw(
                batch,
                enemy.getData().getName(),
                840f + 400f - layout.width,
                685f
        );

        font.getData().setScale(1.4f);
        font.setColor(Color.WHITE);
        font.draw(
                batch,
                "Vidas: " + player.getLives(),
                40f,
                600f
        );
        font.draw(
                batch,
                "Vidas: " + enemy.getLives(),
                1040f,
                600f
        );

        batch.end();
    }

    /** Color de la barra de vida: verde -> amarillo -> rojo según el % restante. */
    private Color healthColor(float pct) {

        pct = Math.max(0f, Math.min(1f, pct));

        if (pct > 0.5f) {
            // verde -> amarillo entre 100% y 50%
            float t = (pct - 0.5f) / 0.5f;
            return new Color(
                    0.9f - 0.75f * t,
                    0.85f,
                    0.2f,
                    1f
            );
        } else {
            // amarillo -> rojo entre 50% y 0%
            float t = pct / 0.5f;
            return new Color(
                    0.9f,
                    0.85f * t,
                    0.2f * t,
                    1f
            );
        }
    }

    /** Barra con marco (bisel) + relleno proporcional, sin cambiar el layout existente. */
    private void drawBar(
            float x, float y, float width, float height,
            float pct, Color fillColor) {

        pct = Math.max(0f, Math.min(1f, pct));

        // bisel exterior
        shapeRenderer.setColor(0.38f, 0.38f, 0.45f, 1f);
        shapeRenderer.rect(x - 3f, y - 3f, width + 6f, height + 6f);

        // fondo vacío
        shapeRenderer.setColor(0.08f, 0.08f, 0.09f, 1f);
        shapeRenderer.rect(x, y, width, height);

        // relleno
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x, y, width * pct, height);
    }

    /** Marco brillante y pulsante para señalar que el especial está listo. */
    private void drawReadyGlow(
            float x, float y, float width, float height, Color glowColor) {

        float pulse =
                (float) (Math.sin(hudPulseTimer * 5.5f) * 0.5 + 0.5);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(
                glowColor.r, glowColor.g, glowColor.b,
                0.35f + 0.45f * pulse
        );

        float pad = 3f + 2.5f * pulse;

        shapeRenderer.rect(x - pad, y - pad, width + pad * 2f, 2.4f);
        shapeRenderer.rect(x - pad, y + height + pad - 2.4f, width + pad * 2f, 2.4f);
        shapeRenderer.rect(x - pad, y - pad, 2.4f, height + pad * 2f);
        shapeRenderer.rect(x + width + pad - 2.4f, y - pad, 2.4f, height + pad * 2f);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // =========================================================
    // REGLAS (antes de la pelea)
    // =========================================================

    private void drawRulesOverlay() {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
                GL20.GL_SRC_ALPHA,
                GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0f, 0f, 0f, 0.82f);
        shapeRenderer.rect(
                0f,
                0f,
                Arena.WORLD_WIDTH,
                Arena.WORLD_HEIGHT
        );

        shapeRenderer.setColor(0.12f, 0.14f, 0.22f, 0.98f);
        shapeRenderer.rect(180f, 110f, 920f, 500f);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.getData().setScale(2.4f);
        font.setColor(Color.WHITE);
        layout.setText(font, "REGLAS DE LA ARENA");
        font.draw(
                batch,
                "REGLAS DE LA ARENA",
                (Arena.WORLD_WIDTH - layout.width) / 2f,
                560f
        );

        font.getData().setScale(1.35f);
        font.setColor(0.95f, 0.95f, 1f, 1f);

        float left = 230f;
        float y = 500f;
        float step = 38f;

        font.draw(batch, "- Si te tiran fuera de la arena, pierdes 1 vida", left, y);
        y -= step;
        font.draw(batch, "- Si te dejan sin vida (barra vacia), tambien pierdes 1 vida", left, y);
        y -= step;
        font.draw(batch, "- Tienes 3 vidas. Gana quien elimine todas las del rival", left, y);
        y -= step;
        font.draw(batch, "- Lo importante es sacar al rival del escenario", left, y);
        y -= step + 10f;

        font.setColor(0.4f, 0.9f, 1f, 1f);
        font.draw(batch, "CONTROLES", left, y);
        y -= step;
        font.setColor(0.95f, 0.95f, 1f, 1f);
        font.draw(batch, "A / D  o  flechas  -  moverse", left, y);
        y -= step;
        font.draw(batch, "ESPACIO  -  saltar (doble salto)", left, y);
        y -= step;
        font.draw(batch, "J  ataque normal   |   K  ataque fuerte   |   L  especial", left, y);
        y -= step + 16f;

        font.getData().setScale(1.2f);
        font.setColor(0.8f, 0.8f, 0.85f, 1f);
        String skip = "La pelea empieza en " +
                Math.max(0, (int) Math.ceil(rulesTimer)) +
                "s   (SPACE/ENTER para saltar)";
        layout.setText(font, skip);
        font.draw(
                batch,
                skip,
                (Arena.WORLD_WIDTH - layout.width) / 2f,
                150f
        );

        batch.end();
    }

    // =========================================================
    // GAME OVER
    // =========================================================

    private void drawGameOver() {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
                GL20.GL_SRC_ALPHA,
                GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.setProjectionMatrix(
                camera.combined
        );

        shapeRenderer.begin(
                ShapeRenderer.ShapeType.Filled
        );

        shapeRenderer.setColor(0f, 0f, 0f, 0.72f);
        shapeRenderer.rect(
                0f,
                0f,
                Arena.WORLD_WIDTH,
                Arena.WORLD_HEIGHT
        );

        if (victory) {
            shapeRenderer.setColor(0.05f, 0.45f, 0.15f, 0.95f);
        } else {
            shapeRenderer.setColor(0.55f, 0.08f, 0.08f, 0.95f);
        }

        shapeRenderer.rect(290f, 240f, 700f, 220f);

        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        String title = victory ? "¡HAS GANADO!" : "GAME OVER";
        String subtitle = victory
                ? "Has derrotado a tu rival"
                : "Te has quedado sin vidas";
        String hint = "ENTER - otra partida    ESC - menu";

        font.getData().setScale(3.2f);
        font.setColor(Color.WHITE);
        layout.setText(font, title);
        font.draw(
                batch,
                title,
                (Arena.WORLD_WIDTH - layout.width) / 2f,
                400f
        );

        font.getData().setScale(1.5f);
        font.setColor(0.9f, 0.9f, 0.9f, 1f);
        layout.setText(font, subtitle);
        font.draw(
                batch,
                subtitle,
                (Arena.WORLD_WIDTH - layout.width) / 2f,
                340f
        );

        font.getData().setScale(1.2f);
        font.setColor(0.75f, 0.75f, 0.8f, 1f);
        layout.setText(font, hint);
        font.draw(
                batch,
                hint,
                (Arena.WORLD_WIDTH - layout.width) / 2f,
                280f
        );

        batch.end();
    }

    // =========================================================
    // RESIZE
    // =========================================================

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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

        player.dispose();

        enemy.dispose();

        batch.dispose();

        shapeRenderer.dispose();

        pixel.dispose();

        font.dispose();
    }
}