package com.tankgame.model;

import java.awt.Rectangle;

public class Bullet {
    public int x;
    public int y;
    public final int dir;
    public final int speed = 10;
    public boolean alive = true;
    public final boolean isPlayer;

    public Bullet(int x, int y, int dir, boolean isPlayer) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.isPlayer = isPlayer;
    }

    public void move() {
        switch (dir) {
            case 0: y -= speed; break;
            case 1: x += speed; break;
            case 2: y += speed; break;
            case 3: x -= speed; break;
        }
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            alive = false;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, 8, 8);
    }
}