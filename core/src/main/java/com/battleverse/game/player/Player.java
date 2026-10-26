package com.battleverse.game.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.battleverse.game.combat.Attack;
import com.battleverse.game.world.Arena;

public class Player {

    // =========================
    // DATOS DEL PERSONAJE
    // =========================

    private final CharacterData data;

    private float x;
    private float y;

    private float velocityX;
    private float velocityY;

    private final float width;
    private final float height;

    // =========================
    // MOVIMIENTO
    // =========================

    private float moveInput = 0f;

    private boolean grounded = false;

    private int jumpsUsed = 0;
    private final int maxJumps = 2;

    // =========================
    // ORIENTACIÓN
    // =========================

    // 1 = derecha
    // -1 = izquierda
    private int facing = 1;

    // =========================
    // COMBATE
    // =========================

    private boolean attacking = false;
    private boolean attackHit = false;

    private Attack currentAttack;

    private float attackTimer = 0f;
    private float attackCooldownTimer = 0f;

    // =========================
    // DAÑO / VIDA
    // =========================

    public static final float MAX_HEALTH = 500f;

    private float damage = 0f;
    private float health = MAX_HEALTH;

    private int lives = 3;

    private float invulnerabilityTimer = 0f;

    // =========================
    // ENERGÍA ESPECIAL
    // =========================

    private float specialEnergy = 0f;

    // =========================
    // ANIMACIÓN
    // =========================

    private final CharacterAnimation animation;

    // =========================
    // CONSTRUCTOR
    // =========================

    public Player(float x, float y, CharacterData data) {

        this.x = x;
        this.y = y;

        this.data = data;

        this.width = data.getWidth();
        this.height = data.getHeight();

        this.velocityX = 0f;
        this.velocityY = 0f;

        this.animation = new CharacterAnimation(data.getName());

        animation.setState(CharacterAnimation.State.IDLE);
    }

    // =========================
    // UPDATE
    // =========================

    public void update(float delta, Arena arena) {

        // -------------------------
        // TEMPORIZADORES
        // -------------------------

        if (attackCooldownTimer > 0f) {
            attackCooldownTimer -= delta;

            if (attackCooldownTimer < 0f) {
                attackCooldownTimer = 0f;
            }
        }

        if (invulnerabilityTimer > 0f) {
            invulnerabilityTimer -= delta;

            if (invulnerabilityTimer < 0f) {
                invulnerabilityTimer = 0f;
            }
        }

        // -------------------------
        // ATAQUE
        // -------------------------

        if (attacking) {

            attackTimer -= delta;

            // El ataque dura hasta que termina la animación del sprite
            if (animation.isCurrentAnimationFinished()
                    || attackTimer <= 0f) {

                attacking = false;
                currentAttack = null;
                attackTimer = 0f;
            }
        }

        // -------------------------
        // MOVIMIENTO HORIZONTAL
        // -------------------------

        float targetSpeed =
                moveInput * data.getSpeed();

        float acceleration =
                data.getAcceleration();

        if (velocityX < targetSpeed) {

            velocityX += acceleration * delta;

            if (velocityX > targetSpeed) {
                velocityX = targetSpeed;
            }

        } else if (velocityX > targetSpeed) {

            velocityX -= acceleration * delta;

            if (velocityX < targetSpeed) {
                velocityX = targetSpeed;
            }
        }

        // -------------------------
        // GRAVEDAD
        // -------------------------

        velocityY -= data.getGravity() * delta;

        // -------------------------
        // POSICIÓN
        // -------------------------

        float previousY = y;

        x += velocityX * delta;
        y += velocityY * delta;

        // Sin paredes laterales: se puede salir del escenario y caer
        // (las vidas se pierden por blast zones en GameScreen).

        // -------------------------
        // PLATAFORMAS
        // -------------------------

        grounded = false;

        arena.resolvePlayer(this, previousY);

        // -------------------------
        // ANIMACIÓN
        // -------------------------

        updateAnimation();

        animation.update(delta);

        // -------------------------
        // INPUT SE CONSUME
        // -------------------------

        moveInput = 0f;
    }

    // =========================
    // ANIMACIÓN
    // =========================

    private void updateAnimation() {

        // Ataque: cada tipo usa su carpeta de sprites (normal/heavy/special)
        if (attacking && currentAttack != null) {
            animation.setState(resolveAttackState(currentAttack));
            return;
        }

        // En el aire: jump al subir, fall al bajar
        if (!grounded) {

            if (velocityY > 0f) {
                animation.setState(CharacterAnimation.State.JUMP);
            } else {
                animation.setState(CharacterAnimation.State.FALL);
            }

            return;
        }

        // En el suelo: walk al moverse, idle quieto
        if (Math.abs(velocityX) > 20f || Math.abs(moveInput) > 0f) {
            animation.setState(CharacterAnimation.State.WALK);
        } else {
            animation.setState(CharacterAnimation.State.IDLE);
        }
    }

    private CharacterAnimation.State resolveAttackState(Attack attack) {

        if (attack.isSpecial() || attack == data.getSpecialAttack()) {
            return CharacterAnimation.State.SPECIAL_ATTACK;
        }

        if (attack == data.getHeavyAttack()) {
            return CharacterAnimation.State.HEAVY_ATTACK;
        }

        return CharacterAnimation.State.NORMAL_ATTACK;
    }

    // =========================
    // MOVER IZQUIERDA
    // =========================

    public void moveLeft() {

        moveInput = -1f;
        facing = -1;
        animation.setFacingRight(false);
    }

    // =========================
    // MOVER DERECHA
    // =========================

    public void moveRight() {

        moveInput = 1f;
        facing = 1;
        animation.setFacingRight(true);
    }

    // =========================
    // SALTO
    // =========================

    public void jump() {

        if (jumpsUsed >= maxJumps) {
            return;
        }

        velocityY = data.getJumpForce();

        jumpsUsed++;

        grounded = false;
    }

    // =========================
    // ATAQUE
    // =========================

    public void attack(Attack attack) {

        if (attack == null) {
            return;
        }

        // No atacar mientras otro ataque está activo
        if (attacking) {
            return;
        }

        // Cooldown
        if (attackCooldownTimer > 0f) {
            return;
        }

        // Energía especial
        if (attack.isSpecial()) {

            if (specialEnergy < attack.getEnergyCost()) {
                return;
            }

            specialEnergy -= attack.getEnergyCost();
        }

        currentAttack = attack;

        attacking = true;

        attackHit = false;

        // Arranca la animación del ataque correspondiente
        CharacterAnimation.State attackState =
                resolveAttackState(attack);

        animation.setState(attackState);

        // Duración = lo que dure la animación (mínimo la del Attack)
        float animDuration =
                animation.getAnimationDuration(attackState);

        attackTimer = Math.max(attack.getDuration(), animDuration);

        attackCooldownTimer = attack.getCooldown();

        // Frenamos un poco al personaje durante el ataque
        velocityX *= 0.35f;
    }

    // =========================
    // DAÑO
    // =========================

    public void addDamage(float amount) {

        damage += amount;

        if (damage > 999f) {
            damage = 999f;
        }

        health -= amount;

        if (health < 0f) {
            health = 0f;
        }
    }

    public void refillHealth() {
        health = MAX_HEALTH;
        damage = 0f;
    }

    public boolean isHealthDepleted() {
        return health <= 0f;
    }

    public void loseLife() {

        lives--;

        refillHealth();

        velocityX = 0f;
        velocityY = 0f;

        attacking = false;
        currentAttack = null;

        attackHit = false;

        invulnerabilityTimer = 1.5f;

        specialEnergy = Math.max(0f, specialEnergy - 25f);
    }

    // =========================
    // RESET
    // =========================

    public void resetPosition(float newX, float newY) {

        x = newX;
        y = newY;

        velocityX = 0f;
        velocityY = 0f;

        grounded = false;

        jumpsUsed = 0;

        attacking = false;

        currentAttack = null;

        attackHit = false;

        attackTimer = 0f;

        refillHealth();

        invulnerabilityTimer = 1.2f;
    }

    // =========================
    // KNOCKBACK
    // =========================

    public void knockback(float forceX, float forceY) {

        velocityX += forceX;
        velocityY += forceY;

        // Evita velocidades absurdas
        if (velocityX > 1400f) {
            velocityX = 1400f;
        }

        if (velocityX < -1400f) {
            velocityX = -1400f;
        }

        if (velocityY > 1100f) {
            velocityY = 1100f;
        }

        if (velocityY < -1200f) {
            velocityY = -1200f;
        }

        grounded = false;
    }

    // =========================
    // ENERGÍA ESPECIAL
    // =========================

    public void addSpecialEnergy(float amount) {

        specialEnergy += amount;

        if (specialEnergy > 100f) {
            specialEnergy = 100f;
        }

        if (specialEnergy < 0f) {
            specialEnergy = 0f;
        }
    }

    // =========================
    // RESET DE SALTOS
    // =========================

    public void resetJumps() {

        jumpsUsed = 0;
        grounded = true;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setGrounded(boolean grounded) {

        this.grounded = grounded;

        if (grounded) {
            jumpsUsed = 0;

            if (velocityY < 0f) {
                velocityY = 0f;
            }
        }
    }

    // =========================
    // DIBUJAR PERSONAJE
    // =========================

    public void draw(Batch batch) {

        animation.draw(
                batch,
                x,
                y,
                width,
                height
        );
    }

    // =========================
    // GETTERS
    // =========================

    public boolean isSpecialAttacking() {

        return attacking
                && currentAttack != null
                && currentAttack.isSpecial();
    }

    public float getAttackProgress() {

        if (!attacking || currentAttack == null) {
            return 0f;
        }

        float total = Math.max(0.01f, currentAttack.getDuration());
        // attackTimer baja: progreso aproximado
        return 1f - Math.max(0f, attackTimer) / Math.max(total, attackTimer + 0.01f);
    }

    public CharacterData getData() {
        return data;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public int getFacing() {
        return facing;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public Attack getCurrentAttack() {
        return currentAttack;
    }

    public boolean hasAttackHit() {
        return attackHit;
    }

    public void setAttackHit(boolean attackHit) {
        this.attackHit = attackHit;
    }

    public float getDamage() {
        return damage;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    public int getLives() {
        return lives;
    }

    public float getSpecialEnergy() {
        return specialEnergy;
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0f;
    }

    // =========================
    // DISPOSE
    // =========================

    public void dispose() {

        animation.dispose();
    }
}