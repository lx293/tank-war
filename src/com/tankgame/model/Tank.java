package com.tankgame.model;

import java.awt.*;

public abstract class Tank {
    public int x, y;
    public int w = 40;
    public int h = 40;
    public int speed = 3;
    public int dir = 0; // 0上 1右 2下 3左
    public boolean alive = true;
    public int shootCD = 0;

    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }

    public boolean canShoot() {
        if (shootCD > 0) {
            shootCD--;
            return false;
        }
        return true;
    }
}