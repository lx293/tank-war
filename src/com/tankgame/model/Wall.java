package com.tankgame.model;

import java.awt.Rectangle;

public class Wall {
    public int x, y, w, h;
    public boolean canBreak;
    public boolean broken;
    public boolean isGrass;
    public boolean isRiver;

    // 普通砖墙/钢墙
    public Wall(int x, int y, int w, int h, boolean canBreak) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.canBreak = canBreak;
        this.broken = false;
        this.isGrass = false;
        this.isRiver = false;
    }

    // 草丛专用
    public Wall(int x, int y, int w, int h, boolean canBreak, boolean isGrass) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.canBreak = canBreak;
        this.broken = false;
        this.isGrass = isGrass;
        this.isRiver = false;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }

    public void hit() {
        if (canBreak) {
            broken = true;
        }
    }
}