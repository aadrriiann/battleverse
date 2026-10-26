package com.battleverse.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CharacterAnimation {

    public enum State {
        IDLE,
        WALK,
        JUMP,
        FALL,
        NORMAL_ATTACK,
        HEAVY_ATTACK,
        SPECIAL_ATTACK
    }

    private final String characterName;

    private final List<Texture> textures = new ArrayList<>();

    private Animation<TextureRegion> idle;
    private Animation<TextureRegion> walk;
    private Animation<TextureRegion> jump;
    private Animation<TextureRegion> fall;
    private Animation<TextureRegion> normalAttack;
    private Animation<TextureRegion> heavyAttack;
    private Animation<TextureRegion> specialAttack;

    private State currentState = State.IDLE;
    private float stateTime = 0f;
    private boolean facingRight = true;

    // Altura de referencia (idle) en píxeles para escalar todos los frames igual
    private float referenceHeight = 1f;

    public CharacterAnimation(String characterName) {
        this.characterName = characterName.toUpperCase();

        String base = "sprites/" + this.characterName + "/";

        idle = loadAnimation(base + "idle", 0.12f, Animation.PlayMode.LOOP);
        walk = loadAnimation(base + "walk", 0.09f, Animation.PlayMode.LOOP);
        jump = loadAnimation(base + "jump", 0.11f, Animation.PlayMode.NORMAL);
        fall = loadAnimation(base + "fall", 0.11f, Animation.PlayMode.NORMAL);

        normalAttack =
                loadAnimation(base + "normal", 0.08f, Animation.PlayMode.NORMAL);

        heavyAttack =
                loadAnimation(base + "heavy", 0.095f, Animation.PlayMode.NORMAL);

        // Timing "con juego": anticipación lenta, carga acelerando,
        // golpe de impacto con hold marcado y salida rápida.
        specialAttack =
                loadAnimation(
                        base + "special",
                        new float[] {
                                0.11f, 0.10f, 0.085f, 0.075f, 0.065f,
                                0.12f, // frame de impacto: se mantiene un pelín más
                                0.05f, 0.05f, 0.06f, 0.08f
                        },
                        Animation.PlayMode.NORMAL
                );

        TextureRegion ref = idle.getKeyFrame(0f);
        referenceHeight = Math.max(1f, ref.getRegionHeight());

        // Brutus tiene frames muy anchos: escala visual un poco menor
        if (this.characterName.equals("BRUTUS")) {
            referenceHeight *= 1.15f;
        }
    }

    private Animation<TextureRegion> loadAnimation(
            String folder,
            float frameDuration,
            Animation.PlayMode playMode) {

        FileHandle directory = Gdx.files.internal(folder);

        if (!directory.exists()) {
            throw new RuntimeException(
                    "No existe la carpeta de animacion: " + folder
            );
        }

        FileHandle[] files = directory.list(".png");

        Arrays.sort(
                files,
                Comparator.comparing(FileHandle::name)
        );

        if (files.length == 0) {
            throw new RuntimeException(
                    "No hay PNG en: " + folder
            );
        }

        TextureRegion[] frames =
                new TextureRegion[files.length];

        for (int i = 0; i < files.length; i++) {

            Texture texture =
                    new Texture(files[i]);

            texture.setFilter(
                    Texture.TextureFilter.Nearest,
                    Texture.TextureFilter.Nearest
            );

            textures.add(texture);

            frames[i] =
                    new TextureRegion(texture);
        }

        Animation<TextureRegion> animation =
                new Animation<TextureRegion>(
                        frameDuration,
                        frames
                );

        animation.setPlayMode(playMode);

        return animation;
    }

    /**
     * Variante compatible con la versión de LibGDX usada por este proyecto.
     * En esta API no existe el constructor que acepta un array de duraciones por
     * frame, así que se aproxima la temporización usando la media del array
     * proporcionado para mantener el comportamiento general sin romper la compilación.
     */
    private Animation<TextureRegion> loadAnimation(
            String folder,
            float[] frameDurations,
            Animation.PlayMode playMode) {

        FileHandle directory = Gdx.files.internal(folder);

        if (!directory.exists()) {
            throw new RuntimeException(
                    "No existe la carpeta de animacion: " + folder
            );
        }

        FileHandle[] files = directory.list(".png");

        Arrays.sort(
                files,
                Comparator.comparing(FileHandle::name)
        );

        if (files.length == 0) {
            throw new RuntimeException(
                    "No hay PNG en: " + folder
            );
        }

        TextureRegion[] frames =
                new TextureRegion[files.length];

        for (int i = 0; i < files.length; i++) {

            Texture texture =
                    new Texture(files[i]);

            texture.setFilter(
                    Texture.TextureFilter.Nearest,
                    Texture.TextureFilter.Nearest
            );

            textures.add(texture);

            frames[i] =
                    new TextureRegion(texture);
        }

        float averageDuration = 0.08f;
        if (frameDurations != null && frameDurations.length > 0) {
            float sum = 0f;
            for (float duration : frameDurations) {
                sum += duration;
            }
            averageDuration = sum / frameDurations.length;
        }

        com.badlogic.gdx.utils.Array<TextureRegion> frameArray =
                new com.badlogic.gdx.utils.Array<>(frames);

        Animation<TextureRegion> animation =
                new Animation<TextureRegion>(
                        averageDuration,
                        frameArray,
                        playMode
                );

        return animation;
    }

    public void setState(State newState) {

        if (newState != currentState) {

            currentState = newState;
            stateTime = 0f;
        }
    }

    public State getState() {
        return currentState;
    }

    public void update(float delta) {

        stateTime += delta;
    }

    public void setFacingRight(boolean facingRight) {

        this.facingRight = facingRight;
    }

    public boolean isFacingRight() {

        return facingRight;
    }

    private Animation<TextureRegion> getAnimation() {
        return animationFor(currentState);
    }

    public boolean isCurrentAnimationFinished() {

        return getAnimation().isAnimationFinished(stateTime);
    }

    public float getAnimationDuration(State state) {

        Animation<TextureRegion> anim = animationFor(state);

        return anim.getAnimationDuration();
    }

    private Animation<TextureRegion> animationFor(State state) {

        switch (state) {

            case WALK:
                return walk;

            case JUMP:
                return jump;

            case FALL:
                return fall;

            case NORMAL_ATTACK:
                return normalAttack;

            case HEAVY_ATTACK:
                return heavyAttack;

            case SPECIAL_ATTACK:
                return specialAttack;

            case IDLE:
            default:
                return idle;
        }
    }

    public void draw(
            Batch batch,
            float x,
            float y,
            float targetWidth,
            float targetHeight) {

        TextureRegion frame =
                getAnimation().getKeyFrame(stateTime);

        // Misma escala para todos los frames (basada en idle),
        // así no se deforman al cambiar de animación.
        float scale = targetHeight / referenceHeight;

        // Especiales más grandes y espectaculares
        if (currentState == State.SPECIAL_ATTACK) {
            scale *= 1.35f;
        }

        float drawWidth =
                frame.getRegionWidth() * scale;

        float drawHeight =
                frame.getRegionHeight() * scale;

        float maxWidth = targetHeight * 2.4f;
        float maxHeight = targetHeight * 1.4f;

        if (currentState == State.SPECIAL_ATTACK) {
            maxWidth = targetHeight * 4.2f;
            maxHeight = targetHeight * 2.1f;
        }

        if (drawWidth > maxWidth || drawHeight > maxHeight) {

            float widthScale = maxWidth / drawWidth;
            float heightScale = maxHeight / drawHeight;
            float clamp = Math.min(widthScale, heightScale);

            drawWidth *= clamp;
            drawHeight *= clamp;
        }

        float drawX =
                x +
                targetWidth / 2f -
                drawWidth / 2f;

        float drawY = y;

        boolean flip = !facingRight;

        if (flip != frame.isFlipX()) {
            frame.flip(true, false);
        }

        batch.draw(
                frame,
                drawX,
                drawY,
                drawWidth,
                drawHeight
        );
    }

    public void dispose() {

        for (Texture texture : textures) {
            texture.dispose();
        }

        textures.clear();
    }
}
