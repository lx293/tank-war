package com.tankgame.model;

import java.awt.Rectangle;

public class Item {
    public final int x;
    public final int y;
    public final int type;
    public boolean alive = true;

    // 补上构造方法，初始化final变量
    public Item(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, 20, 20);
    }
}