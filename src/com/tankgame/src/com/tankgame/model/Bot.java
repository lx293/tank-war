package com.tankgame.model;

import java.util.List;
import java.awt.Rectangle;
import java.util.Random;

public class Bot {
    public int x, y, dir = 0, speed = 2;
    public boolean alive = true;
    public long lastShot = 0;
    public int shotDelay = 1000;
    public Random rand = new Random();

    public Bot(int x, int y) {
        this.x = x;
        this.y = y;
        dir = rand.nextInt(4);
    }

    public void autoMove(List<Wall> walls, List<Bot> enemies, PlayerOne player) {
        int dx = 0, dy = 0;
        switch (dir) {
            case 0: dy = -speed; break;
            case 1: dx = speed; break;
            case 2: dy = speed; break;
            case 3: dx = -speed; break;
        }
        int nx = x + dx, ny = y + dy;
        Rectangle newRect = new Rectangle(nx, ny, 40, 40);
        boolean canMove = true;

        for (Wall w : walls) {
            if (!w.broken && newRect.intersects(w.getRect())) {
                canMove = false;
                break;
            }
        }
        if (canMove) {
            for (Bot bot : enemies) {
                if (bot != this && bot.alive && newRect.intersects(bot.getRect())) {
                    canMove = false;
                    break;
                }
            }
        }
        if (canMove && player.alive && newRect.intersects(player.getRect())) {
            canMove = false;
        }

        if (canMove) {
            x = nx;
            y = ny;
        } else {
            dir = rand.nextInt(4);
        }
        if (rand.nextInt(100) == 0) {
            dir = rand.nextInt(4);
        }
    }

    public void autoShoot(List<Bullet> bullets) {
        if (System.currentTimeMillis() - lastShot > shotDelay) {
            lastShot = System.currentTimeMillis();
            int cx = x + 20, cy = y + 20;
            bullets.add(new Bullet(cx - 4, cy - 4, dir, false));
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, 40, 40);
    }
}