package com.tankgame.model;

public class PlayerOne extends Tank {
    public int maxHp = 3;
    public int hp = maxHp;
    public int life = 3;

    public PlayerOne(int x, int y) {
        super(x, y);
        this.speed = 3;
    }

    public Bullet shoot() {
        if (canShoot()) {
            shootCD = 20;
            return new Bullet(x + 15, y + 15, dir, true);
        }
        return null;
    }

    public void hurt(int damage) {
        hp -= damage;
        if (hp <= 0) {
            life--;
            if (life > 0) {
                hp = maxHp;
                x = 380;
                y = 500;
            } else {
                alive = false;
            }
        }
    }
}