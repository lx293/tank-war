package com.tankgame.model;

import java.util.List;
import java.awt.Rectangle;

public class PlayerOne {
    public int x, y, dir = 0, speed = 5;
    public boolean alive = true;
    public long lastShot = 0;
    public int shotDelay = 300;

    public int maxHp = 3;
    public int hp = 3;
    public int life = 3;

    public PlayerOne(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void hurt(int damage) {
        hp -= damage;
        if (hp <= 0) {
            alive = false;
            life--;
            if (life > 0) {
                revive();
            }
        }
    }

    public void revive() {
        hp = maxHp;
        alive = true;
        x = 400 - 20;
        y = 600 - 60;
        dir = 0;
    }

    public void move(int dx, int dy, List<Wall> walls) {
        if (!alive) return;
        int nx = x + dx, ny = y + dy;
        Rectangle r = new Rectangle(nx, ny, 40, 40);
        boolean ok = true;
        for (Wall w : walls) {
            if (!w.broken && r.intersects(w.getRect())) {
                ok = false;
                break;
            }
        }
        if (ok) {
            x = nx;
            y = ny;
        }
    }

    public boolean canShoot() {
        return alive && System.currentTimeMillis() - lastShot > shotDelay;
    }

    public Bullet shoot() {
        lastShot = System.currentTimeMillis();
        int cx = x + 20, cy = y + 20;
        return new Bullet(cx - 4, cy - 4, dir, true);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, 40, 40);
    }

    public void updateBullets(List<Bullet> bullets) {}
    public void useItem(int type) {}
}