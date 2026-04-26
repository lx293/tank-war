package com.tankgame.model;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Boss {
    public int x, y;
    public int w = 60, h = 60;
    public int hp = 30;
    public int speed = 2;
    public int dir;
    private int moveTick = 0;
    private int shootTick = 0;
    private final Random rand = new Random();

    public Boss(int x, int y) {
        this.x = x;
        this.y = y;
        this.dir = 2;
    }

    public void autoMove(List<Wall> walls, PlayerOne player, List<Bot> enemies) {
        moveTick++;
        if (moveTick > 50) {
            moveTick = 0;
            dir = rand.nextInt(4);
        }

        int nx = x;
        int ny = y;

        switch (dir) {
            case 0 -> ny -= speed;
            case 1 -> nx += speed;
            case 2 -> ny += speed;
            case 3 -> nx -= speed;
        }

        // 边界检查
        if (nx < 20 || nx > 800 - 60 || ny < 20 || ny > 600 - 60) {
            dir = rand.nextInt(4);
            return;
        }

        Rectangle newRect = new Rectangle(nx, ny, w, h);
        boolean canMove = true;

        // 墙碰撞
        for (Wall wall : walls) {
            if (!wall.isGrass && !wall.broken && newRect.intersects(wall.getRect())) {
                canMove = false;
                dir = rand.nextInt(4);
                break;
            }
        }

        // 不穿玩家
        if (canMove && player.alive && newRect.intersects(player.getRect())) {
            canMove = false;
            dir = rand.nextInt(4);
        }

        // 不穿小兵
        if (canMove) {
            for (Bot b : enemies) {
                if (b.alive && newRect.intersects(b.getRect())) {
                    canMove = false;
                    dir = rand.nextInt(4);
                    break;
                }
            }
        }

        if (canMove) {
            x = nx;
            y = ny;
        }
    }

    // 炮弹随方向发射
    public void autoShoot(List<Bullet> bullets) {
        shootTick++;
        if (shootTick >= 40) {
            shootTick = 0;
            int bx = x + w/2 - 4;
            int by = y + h/2 - 4;
            bullets.add(new Bullet(bx, by, dir, false));
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }
}