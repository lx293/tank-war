package com.tankgame.model;

import java.awt.Rectangle;

public class Item {
    public int x, y, type;
    public boolean alive = true;

    public Item(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, 20, 20);
    }
}