package com.battleverse.game.player;

import com.badlogic.gdx.graphics.Color;
import com.battleverse.game.combat.Attack;

public class CharacterData {

    private final String name;
    private final Color color;

    private final float speed;
    private final float acceleration;
    private final float jumpForce;
    private final float gravity;
    private final float weight;

    private final float width;
    private final float height;

    private final Attack normalAttack;
    private final Attack heavyAttack;
    private final Attack specialAttack;

    public CharacterData(
            String name,
            Color color,
            float speed,
            float acceleration,
            float jumpForce,
            float gravity,
            float weight,
            float width,
            float height,
            Attack normalAttack,
            Attack heavyAttack,
            Attack specialAttack) {

        this.name = name;
        this.color = color;

        this.speed = speed;
        this.acceleration = acceleration;
        this.jumpForce = jumpForce;
        this.gravity = gravity;
        this.weight = weight;

        this.width = width;
        this.height = height;

        this.normalAttack = normalAttack;
        this.heavyAttack = heavyAttack;
        this.specialAttack = specialAttack;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public float getGravity() {
        return gravity;
    }

    public float getWeight() {
        return weight;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Attack getNormalAttack() {
        return normalAttack;
    }

    public Attack getHeavyAttack() {
        return heavyAttack;
    }

    public Attack getSpecialAttack() {
        return specialAttack;
    }

    // ---------------------------------------------------------
    // PERSONAJES
    // ---------------------------------------------------------

    public static CharacterData rex() {

        return new CharacterData(
                "REX",
                Color.RED,

                430f,
                2400f,
                900f,
                1800f,
                1.0f,

                38f,
                52f,

                new Attack(
                        "Golpe",
                        18f,
                        420f,
                        70f,
                        115f,
                        90f,
                        0.12f,
                        0.25f,
                        0f,
                        false
                ),

                new Attack(
                        "Golpe fuerte",
                        32f,
                        560f,
                        100f,
                        105f,
                        100f,
                        0.20f,
                        0.45f,
                        0f,
                        false
                ),

                new Attack(
                        "Ráfaga roja",
                        95f,
                        920f,
                        220f,
                        195f,
                        130f,
                        0.28f,
                        0.75f,
                        50f,
                        true
                )
        );
    }

    public static CharacterData volt() {

        return new CharacterData(
                "VOLT",
                Color.BLUE,

                570f,
                3000f,
                870f,
                1850f,
                0.85f,

                34f,
                50f,

                new Attack(
                        "Golpe eléctrico",
                        16f,
                        380f,
                        55f,
                        125f,
                        85f,
                        0.10f,
                        0.20f,
                        0f,
                        false
                ),

                new Attack(
                        "Impacto eléctrico",
                        28f,
                        500f,
                        90f,
                        115f,
                        95f,
                        0.17f,
                        0.40f,
                        0f,
                        false
                ),

                new Attack(
                        "Rayo",
                        85f,
                        980f,
                        200f,
                        210f,
                        120f,
                        0.24f,
                        0.60f,
                        50f,
                        true
                )
        );
    }

    public static CharacterData nyx() {

        return new CharacterData(
                "NYX",
                new Color(0.55f, 0.15f, 0.85f, 1f),

                400f,
                2200f,
                940f,
                1750f,
                0.95f,

                36f,
                52f,

                new Attack(
                        "Corte sombrío",
                        17f,
                        400f,
                        60f,
                        125f,
                        95f,
                        0.13f,
                        0.25f,
                        0f,
                        false
                ),

                new Attack(
                        "Corte pesado",
                        30f,
                        530f,
                        85f,
                        120f,
                        105f,
                        0.20f,
                        0.45f,
                        0f,
                        false
                ),

                new Attack(
                        "Explosión oscura",
                        90f,
                        900f,
                        180f,
                        220f,
                        120f,
                        0.28f,
                        0.80f,
                        50f,
                        true
                )
        );
    }

    public static CharacterData brutus() {

        return new CharacterData(
                "BRUTUS",
                Color.ORANGE,

                315f,
                1800f,
                790f,
                1850f,
                1.45f,

                40f,
                46f,

                new Attack(
                        "Puñetazo",
                        22f,
                        470f,
                        70f,
                        135f,
                        105f,
                        0.16f,
                        0.30f,
                        0f,
                        false
                ),

                new Attack(
                        "Golpe demoledor",
                        38f,
                        650f,
                        110f,
                        145f,
                        115f,
                        0.24f,
                        0.50f,
                        0f,
                        false
                ),

                new Attack(
                        "Furia",
                        110f,
                        1050f,
                        240f,
                        190f,
                        130f,
                        0.32f,
                        1.00f,
                        50f,
                        true
                )
        );
    }

    public static CharacterData[] getAllCharacters() {

        return new CharacterData[]{
                rex(),
                volt(),
                nyx(),
                brutus()
        };
    }

    /** Rival de arcade: cualquier personaje distinto al del jugador. */
    public static CharacterData randomOpponent(String playerName) {

        CharacterData[] all = getAllCharacters();

        java.util.ArrayList<CharacterData> options =
                new java.util.ArrayList<>();

        for (CharacterData character : all) {

            if (!character.getName().equalsIgnoreCase(playerName)) {
                options.add(character);
            }
        }

        if (options.isEmpty()) {
            return brutus();
        }

        int index =
                (int) (Math.random() * options.size());

        return options.get(index);
    }
}