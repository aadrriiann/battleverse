package com.battleverse.game.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battleverse.game.player.Player;
import com.battleverse.game.world.Arena;

/**
 * Efectos visuales potentes para ataques especiales.
 */
public class SpecialVfx {

    private static class Spark {
        float x, y;
        float vx, vy;
        float life;
        float maxLife;
        float size;
        Color color = new Color();
    }

    private static class Ring {
        float x, y;
        float radius;
        float maxRadius;
        float life;
        Color color = new Color();
    }

    private static class Beam {
        float x, y;
        float facing;
        float life;
        float length;
        Color color = new Color();
        String style;
    }

    private final Array<Spark> sparks = new Array<>();
    private final Array<Ring> rings = new Array<>();
    private final Array<Beam> beams = new Array<>();

    private float screenFlash = 0f;
    private final Color flashColor = new Color(1f, 1f, 1f, 0f);

    private Player activeCaster;
    private float casterPulse = 0f;

    public void startSpecial(Player caster) {

        activeCaster = caster;
        casterPulse = 0f;

        Color c = colorFor(caster);

        flashColor.set(c.r, c.g, c.b, 0.55f);
        screenFlash = 0.35f;

        float cx = caster.getX() + caster.getWidth() / 2f;
        float cy = caster.getY() + caster.getHeight() * 0.55f;

        spawnRing(cx, cy, 20f, 140f, c);
        spawnBurst(cx, cy, c, 28);

        Beam beam = new Beam();
        beam.x = cx;
        beam.y = cy;
        beam.facing = caster.getFacing();
        beam.life = 0.85f;
        beam.length = 220f;
        beam.color.set(c);
        beam.style = caster.getData().getName().toUpperCase();
        beams.add(beam);
    }

    public void onSpecialHit(Player attacker, Player target) {

        Color c = colorFor(attacker);

        flashColor.set(1f, 1f, 1f, 0.7f);
        screenFlash = 0.22f;

        float tx = target.getX() + target.getWidth() / 2f;
        float ty = target.getY() + target.getHeight() * 0.5f;

        spawnRing(tx, ty, 10f, 160f, c);
        spawnBurst(tx, ty, c, 40);
        spawnBurst(tx, ty, Color.WHITE, 12);
    }

    public void update(float delta, Player player, Player enemy) {

        if (screenFlash > 0f) {
            screenFlash -= delta;
            if (screenFlash < 0f) {
                screenFlash = 0f;
            }
        }

        casterPulse += delta;

        // Aura mientras el especial está activo
        if (player != null && player.isSpecialAttacking()) {
            pulseAura(player, delta);
            activeCaster = player;
        } else if (enemy != null && enemy.isSpecialAttacking()) {
            pulseAura(enemy, delta);
            activeCaster = enemy;
        } else if (activeCaster != null
                && !activeCaster.isSpecialAttacking()) {
            activeCaster = null;
        }

        for (int i = sparks.size - 1; i >= 0; i--) {

            Spark s = sparks.get(i);
            s.life -= delta;
            s.x += s.vx * delta;
            s.y += s.vy * delta;
            s.vy -= 400f * delta;

            if (s.life <= 0f) {
                sparks.removeIndex(i);
            }
        }

        for (int i = rings.size - 1; i >= 0; i--) {

            Ring r = rings.get(i);
            r.life -= delta;
            float progress = 1f - Math.max(0f, r.life);
            r.radius = r.maxRadius * Math.min(1f, progress * 1.4f);

            if (r.life <= 0f) {
                rings.removeIndex(i);
            }
        }

        for (int i = beams.size - 1; i >= 0; i--) {

            Beam b = beams.get(i);
            b.life -= delta;

            if (b.life <= 0f) {
                beams.removeIndex(i);
            }
        }
    }

    private void pulseAura(Player caster, float delta) {

        if (MathUtils.randomBoolean(0.35f)) {

            Color c = colorFor(caster);
            float cx = caster.getX() + caster.getWidth() / 2f;
            float cy = caster.getY() + caster.getHeight() * 0.5f;

            Spark s = new Spark();
            s.x = cx + MathUtils.random(-18f, 18f);
            s.y = cy + MathUtils.random(-10f, 24f);
            s.vx = MathUtils.random(-40f, 40f);
            s.vy = MathUtils.random(60f, 180f);
            s.life = MathUtils.random(0.25f, 0.55f);
            s.maxLife = s.life;
            s.size = MathUtils.random(3f, 7f);
            s.color.set(c);
            sparks.add(s);
        }
    }

    public void draw(ShapeRenderer shapes) {

        com.badlogic.gdx.Gdx.gl.glEnable(GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(
                GL20.GL_SRC_ALPHA,
                GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Flash de pantalla
        if (screenFlash > 0f) {

            float a = Math.min(0.55f, screenFlash * 1.6f);
            shapes.setColor(
                    flashColor.r,
                    flashColor.g,
                    flashColor.b,
                    a
            );
            shapes.rect(
                    0f,
                    0f,
                    Arena.WORLD_WIDTH,
                    Arena.WORLD_HEIGHT
            );
        }

        // Aura del caster
        if (activeCaster != null && activeCaster.isSpecialAttacking()) {

            Color c = colorFor(activeCaster);
            float cx = activeCaster.getX() + activeCaster.getWidth() / 2f;
            float cy = activeCaster.getY() + activeCaster.getHeight() * 0.5f;
            float pulse = 28f + MathUtils.sin(casterPulse * 18f) * 8f;

            shapes.setColor(c.r, c.g, c.b, 0.22f);
            shapes.circle(cx, cy, pulse + 18f);
            shapes.setColor(c.r, c.g, c.b, 0.4f);
            shapes.circle(cx, cy, pulse);
            shapes.setColor(1f, 1f, 1f, 0.35f);
            shapes.circle(cx, cy, pulse * 0.45f);
        }

        // Rayos / ondas hacia delante
        for (Beam b : beams) {

            float alpha = MathUtils.clamp(b.life * 1.4f, 0f, 0.85f);
            float tipX = b.x + b.facing * b.length * (1.1f - b.life * 0.3f);
            float midY = b.y;

            if ("VOLT".equals(b.style)) {

                shapes.setColor(0.4f, 0.85f, 1f, alpha);
                drawLightning(shapes, b.x, b.y, tipX, midY, 5);
                shapes.setColor(1f, 1f, 1f, alpha * 0.8f);
                drawLightning(shapes, b.x, b.y, tipX, midY - 6f, 2);

            } else if ("NYX".equals(b.style)) {

                shapes.setColor(0.55f, 0.15f, 0.9f, alpha * 0.7f);
                shapes.ellipse(
                        Math.min(b.x, tipX),
                        midY - 35f,
                        Math.abs(tipX - b.x),
                        70f
                );
                shapes.setColor(0.9f, 0.5f, 1f, alpha);
                shapes.ellipse(
                        Math.min(b.x, tipX) + 20f,
                        midY - 18f,
                        Math.abs(tipX - b.x) * 0.7f,
                        36f
                );

            } else if ("BRUTUS".equals(b.style)) {

                shapes.setColor(1f, 0.55f, 0.1f, alpha * 0.75f);
                shapes.ellipse(
                        tipX - 50f,
                        midY - 40f,
                        100f,
                        70f
                );
                shapes.setColor(1f, 0.85f, 0.3f, alpha);
                shapes.circle(tipX, midY, 28f + (0.85f - b.life) * 40f);

            } else {
                // REX - ola roja
                shapes.setColor(1f, 0.2f, 0.15f, alpha * 0.8f);
                shapes.ellipse(
                        Math.min(b.x, tipX),
                        midY - 28f,
                        Math.abs(tipX - b.x),
                        56f
                );
                shapes.setColor(1f, 0.7f, 0.2f, alpha);
                shapes.ellipse(
                        Math.min(b.x, tipX) + 10f,
                        midY - 14f,
                        Math.abs(tipX - b.x) * 0.75f,
                        28f
                );
            }
        }

        for (Ring r : rings) {

            float a = MathUtils.clamp(r.life * 2f, 0f, 0.65f);
            shapes.setColor(r.color.r, r.color.g, r.color.b, a);
            shapes.circle(r.x, r.y, r.radius);
            shapes.setColor(1f, 1f, 1f, a * 0.5f);
            shapes.circle(r.x, r.y, r.radius * 0.55f);
        }

        for (Spark s : sparks) {

            float a = MathUtils.clamp(s.life / s.maxLife, 0f, 1f);
            shapes.setColor(s.color.r, s.color.g, s.color.b, a);
            shapes.circle(s.x, s.y, s.size * a);
        }

        shapes.end();

        com.badlogic.gdx.Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawLightning(
            ShapeRenderer shapes,
            float x0,
            float y0,
            float x1,
            float y1,
            float thickness) {

        float dx = x1 - x0;
        float dy = y1 - y0;
        int segments = 7;

        float prevX = x0;
        float prevY = y0;

        for (int i = 1; i <= segments; i++) {

            float t = i / (float) segments;
            float x = x0 + dx * t;
            float y = y0 + dy * t;

            if (i < segments) {
                y += MathUtils.random(-22f, 22f);
                x += MathUtils.random(-6f, 6f);
            }

            shapes.rectLine(prevX, prevY, x, y, thickness);
            prevX = x;
            prevY = y;
        }
    }

    private void spawnBurst(float x, float y, Color c, int count) {

        for (int i = 0; i < count; i++) {

            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(120f, 420f);

            Spark s = new Spark();
            s.x = x;
            s.y = y;
            s.vx = MathUtils.cos(angle) * speed;
            s.vy = MathUtils.sin(angle) * speed;
            s.life = MathUtils.random(0.3f, 0.7f);
            s.maxLife = s.life;
            s.size = MathUtils.random(3f, 9f);
            s.color.set(c);
            sparks.add(s);
        }
    }

    private void spawnRing(
            float x,
            float y,
            float start,
            float max,
            Color c) {

        Ring r = new Ring();
        r.x = x;
        r.y = y;
        r.radius = start;
        r.maxRadius = max;
        r.life = 0.45f;
        r.color.set(c);
        rings.add(r);
    }

    private Color colorFor(Player p) {

        String name = p.getData().getName().toUpperCase();

        switch (name) {

            case "VOLT":
                return new Color(0.35f, 0.8f, 1f, 1f);

            case "NYX":
                return new Color(0.65f, 0.2f, 0.95f, 1f);

            case "BRUTUS":
                return new Color(1f, 0.55f, 0.15f, 1f);

            case "REX":
            default:
                return new Color(1f, 0.25f, 0.18f, 1f);
        }
    }
}
