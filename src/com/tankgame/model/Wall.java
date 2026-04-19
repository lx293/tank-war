package com.tankgame.model;

import java.awt.Rectangle;

public class Wall {
    public int x, y, w, h;
    public boolean canBreak, broken = false;

    public Wall(int x, int y, int w, int h, boolean canBreak) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.canBreak = canBreak;
    }

    public void hit() {
        if (canBreak) {
            broken = true;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }
}