package com.tankgame.model;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Bot extends Tank {
    private final Random rand = new Random();

    public Bot(int x, int y) {
        super(x, y);
        this.speed = 2;
        this.dir = 2;
    }

    public void autoMove(List<Wall> walls, List<Bot> enemies, PlayerOne player) {
        // 随机转向
        if (rand.nextInt(100) < 4) {
            dir = rand.nextInt(4);
        }

        int nx = x;
        int ny = y;
        switch (dir) {
            case 0: ny -= speed; break;
            case 1: nx += speed; break;
            case 2: ny += speed; break;
            case 3: nx -= speed; break;
        }

        // 边界预判
        if (nx < 20 || nx > 760 || ny < 20 || ny > 560) {
            dir = rand.nextInt(4);
            return;
        }

        Rectangle newRect = new Rectangle(nx, ny, w, h);
        boolean canMove = true;

        // 墙碰撞预判
        for (Wall w : walls) {
            if (!w.broken && !w.isGrass && newRect.intersects(w.getRect())) {
                canMove = false;
                dir = rand.nextInt(4);
                break;
            }
        }

        // 敌人之间不重叠
        if (canMove) {
            for (Bot other : enemies) {
                if (other != this && other.alive && newRect.intersects(other.getRect())) {
                    canMove = false;
                    dir = rand.nextInt(4);
                    break;
                }
            }
        }

        // 不穿玩家
        if (canMove && newRect.intersects(player.getRect())) {
            canMove = false;
            dir = rand.nextInt(4);
        }

        // 只有全部通过才移动
        if (canMove) {
            x = nx;
            y = ny;
        }
    }

    public void autoShoot(List<Bullet> bullets) {
        if (canShoot()) {
            int bx = x + w/2 - 4;
            int by = y + h/2 - 4;
            bullets.add(new Bullet(bx, by, dir, false));
            shootCD = 60;
        }
    }
}
