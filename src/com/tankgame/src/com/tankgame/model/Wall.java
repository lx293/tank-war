package com.tankgame.model;

import java.awt.Rectangle;

public class Wall {
    public final int x;
    public final int y;
    public final int w;
    public final int h;
    public final boolean canBreak;
    public boolean broken = false;

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