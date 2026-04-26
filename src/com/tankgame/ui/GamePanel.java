package com.tankgame.ui;

import com.tankgame.audio.SoundManager;
import com.tankgame.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    public static final int W = 800, H = 600;
    private final int TILE = 20;
    private int mode = 0;
    private BufferedImage modeImg;
    PlayerOne player;
    List<Bot> enemies = new ArrayList<>();
    List<Wall> walls = new ArrayList<>();
    List<Bullet> bullets = new ArrayList<>();
    Boss boss = null;
    private final boolean[] keys = new boolean[256];
    boolean gameOver = false, isPaused = false, showEndMenu = false, showShop = false;
    int score = 0;

    // 全局总敌人数（含BOSS）
    int totalEnemies = 0;
    int remainingEnemies = 0;

    // 波次控制（内部用，不显示）
    int wave = 0;

    // 道具状态
    int speedTime = 0;
    int doubleBulletTime = 0;
    int shieldTime = 0;

    private final String[] shopNames = {"满血(100分)", "加速(150分)", "双弹(200分)", "无敌(300分)"};
    private final int[] shopPrices = {100, 150, 200, 300};

    private Timer gameTimer;
    private JDialog modeDialog;
    private final SoundManager soundManager;

    public GamePanel() {
        setBackground(new Color(50, 50, 50));
        setFocusable(true);
        requestFocus();
        setDoubleBuffered(true);
        loadModeImage();
        soundManager = SoundManager.getInstance();
        showModeDialog();
    }

    private void loadModeImage() {
        try {
            modeImg = ImageIO.read(new File("images/ndxz.PNG"));
        } catch (Exception ignored) {}
    }

    private void showModeDialog() {
        modeDialog = new JDialog((JFrame) null, "选择难度", true);
        modeDialog.setSize(800, 600);
        modeDialog.setLocationRelativeTo(null);
        modeDialog.setResizable(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (modeImg != null) g.drawImage(modeImg, 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(null);

        JButton easy = new JButton("简单");
        JButton normal = new JButton("普通");
        JButton hard = new JButton("困难（含BOSS）");
        easy.setBounds(320, 200, 160, 50);
        normal.setBounds(320, 280, 160, 50);
        hard.setBounds(320, 360, 160, 50);
        easy.setFont(new Font("黑体", Font.BOLD, 22));
        normal.setFont(new Font("黑体", Font.BOLD, 22));
        hard.setFont(new Font("黑体", Font.BOLD, 22));

        easy.addActionListener(_ -> { mode = 0; modeDialog.dispose(); startGame(); });
        normal.addActionListener(_ -> { mode = 1; modeDialog.dispose(); startGame(); });
        hard.addActionListener(_ -> { mode = 2; modeDialog.dispose(); startGame(); });

        panel.add(easy);
        panel.add(normal);
        panel.add(hard);
        modeDialog.add(panel);
        modeDialog.setVisible(true);
    }

    public void startGame() {
        gameOver = false;
        isPaused = false;
        showEndMenu = false;
        showShop = false;
        score = 0;
        speedTime = 0;
        doubleBulletTime = 0;
        shieldTime = 0;
        wave = 0;

        enemies.clear();
        walls.clear();
        bullets.clear();
        boss = null;

        // 按模式设置总敌人数（含BOSS）
        if (mode == 0) {
            totalEnemies = 7; // 6普通+1BOSS
        } else if (mode == 1) {
            totalEnemies = 13; // 12普通+1BOSS
        } else {
            totalEnemies = 21; // 20普通+1BOSS
        }
        remainingEnemies = totalEnemies;

        player = new PlayerOne(W/2-20, H-60);
        initMap();
        spawnWave();
        initKeyListener();
        startTimer();
        soundManager.playBackgroundMusic();
    }

    // ========== 波次刷新逻辑（内部用） ==========
    private void spawnWave() {
        enemies.clear();
        wave++;

        if (mode == 0) {
            // 简单：2波普通，最后BOSS
            if (wave == 1) {
                enemies.add(new Bot(100, 100));
                enemies.add(new Bot(400, 100));
                enemies.add(new Bot(660, 100));
            } else if (wave == 2) {
                enemies.add(new Bot(150, 100));
                enemies.add(new Bot(400, 100));
                enemies.add(new Bot(600, 100));
            } else if (wave == 3) {
                boss = new Boss(370, 50);
                boss.hp = 10;
            }
        } else if (mode == 1) {
            // 普通：3波普通，最后BOSS
            if (wave == 1) {
                enemies.add(new Bot(120, 100));
                enemies.add(new Bot(260, 100));
                enemies.add(new Bot(500, 100));
                enemies.add(new Bot(640, 100));
            } else if (wave == 2) {
                enemies.add(new Bot(100, 120));
                enemies.add(new Bot(300, 120));
                enemies.add(new Bot(460, 120));
                enemies.add(new Bot(660, 120));
            } else if (wave == 3) {
                enemies.add(new Bot(140, 100));
                enemies.add(new Bot(280, 100));
                enemies.add(new Bot(520, 100));
                enemies.add(new Bot(660, 100));
            } else if (wave == 4) {
                boss = new Boss(370, 50);
                boss.hp = 20;
            }
        } else if (mode == 2) {
            // 困难：4波普通，最后BOSS
            if (wave == 1) {
                enemies.add(new Bot(80, 80));
                enemies.add(new Bot(200, 80));
                enemies.add(new Bot(340, 80));
                enemies.add(new Bot(480, 80));
                enemies.add(new Bot(660, 80));
            } else if (wave == 2) {
                enemies.add(new Bot(100, 100));
                enemies.add(new Bot(240, 100));
                enemies.add(new Bot(380, 100));
                enemies.add(new Bot(520, 100));
                enemies.add(new Bot(680, 100));
            } else if (wave == 3) {
                enemies.add(new Bot(120, 80));
                enemies.add(new Bot(260, 80));
                enemies.add(new Bot(400, 80));
                enemies.add(new Bot(540, 80));
                enemies.add(new Bot(680, 80));
            } else if (wave == 4) {
                enemies.add(new Bot(140, 100));
                enemies.add(new Bot(280, 100));
                enemies.add(new Bot(420, 100));
                enemies.add(new Bot(560, 100));
                enemies.add(new Bot(700, 100));
            } else if (wave == 5) {
                boss = new Boss(370, 50);
                boss.hp = 30;
            }
        }
    }

    // ========== 地图 ==========
    private void initMap() {
        for (int x = 0; x < W; x += TILE) {
            walls.add(new Wall(x, 0, TILE, TILE, false));
            walls.add(new Wall(x, H - TILE, TILE, TILE, false));
        }
        for (int y = 0; y < H; y += TILE) {
            walls.add(new Wall(0, y, TILE, TILE, false));
            walls.add(new Wall(W - TILE, y, TILE, TILE, false));
        }

        if (mode == 0) {
            addRect(280, 200, 4, 3, true);
            addRect(480, 200, 4, 3, true);
            addRect(360, 300, 6, 2, true);
            addGrass(100, 240, 5, 3);
            addGrass(650, 240, 5, 3);
        } else if (mode == 1) {
            addRect(200, 160, 3, 10, true);
            addRect(570, 160, 3, 10, true);
            addRect(340, 180, 4, 4, true);
            addRect(420, 180, 4, 4, true);
            addRect(260, 320, 8, 2, true);
            addRect(260, 380, 2, 4, true);
            addRect(520, 380, 2, 4, true);
            addRect(380, 260, 2, 6, false);
            addGrass(120, 260, 6, 4);
            addGrass(620, 260, 6, 4);
            addGrass(220, 400, 8, 3);
        } else if (mode == 2) {
            addRect(150, 140, 2, 14, true);
            addRect(220, 140, 2, 14, true);
            addRect(630, 140, 2, 14, true);
            addRect(560, 140, 2, 14, true);
            addRect(300, 150, 4, 2, true);
            addRect(460, 150, 4, 2, true);
            addRect(340, 220, 2, 8, true);
            addRect(440, 220, 2, 8, true);
            addRect(300, 320, 10, 2, true);
            addRect(370, 180, 2, 4, false);
            addRect(410, 180, 2, 4, false);
            addRect(270, 280, 2, 2, false);
            addRect(510, 280, 2, 2, false);
            addGrass(80, 220, 8, 5);
            addGrass(640, 220, 8, 5);
            addGrass(100, 380, 6, 4);
            addGrass(640, 380, 6, 4);
        }

        if (mode == 0) {
            addRect(320, 480, 8, 1, true);
            addRect(320, 500, 1, 4, true);
            addRect(460, 500, 1, 4, true);
        } else if (mode == 1) {
            addRect(320, 480, 8, 1, true);
            addRect(320, 500, 1, 3, true);
            addRect(460, 500, 1, 3, true);
        } else {
            addRect(330, 480, 6, 1, true);
            addRect(330, 500, 1, 2, true);
            addRect(450, 500, 1, 2, true);
        }
    }

    private void addRect(int x, int y, int w, int h, boolean canBreak) {
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                walls.add(new Wall(x + i * TILE, y + j * TILE, TILE, TILE, canBreak));
    }

    private void addGrass(int x, int y, int w, int h) {
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                walls.add(new Wall(x + i * TILE, y + j * TILE, TILE, TILE, false, true));
    }

    private void initKeyListener() {
        for (KeyListener kl : getKeyListeners()) removeKeyListener(kl);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (showEndMenu) {
                    if (e.getKeyCode() == KeyEvent.VK_1) startGame();
                    if (e.getKeyCode() == KeyEvent.VK_2) showModeDialog();
                    if (e.getKeyCode() == KeyEvent.VK_3) System.exit(0);
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    showShop = !showShop;
                    isPaused = showShop;
                    return;
                }
                if (showShop) {
                    if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_4) {
                        int idx = e.getKeyCode() - KeyEvent.VK_1;
                        buyItem(idx);
                    }
                    return;
                }
                if (e.getKeyCode() < keys.length) keys[e.getKeyCode()] = true;
                if (e.getKeyCode() == KeyEvent.VK_P) isPaused = !isPaused;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() < keys.length) keys[e.getKeyCode()] = false;
            }
        });
    }

    private void buyItem(int idx) {
        if (score < shopPrices[idx]) return;
        score -= shopPrices[idx];

        switch (idx) {
            case 0 -> player.hp = player.maxHp;
            case 1 -> speedTime = 600;
            case 2 -> doubleBulletTime = 600;
            case 3 -> shieldTime = 480;
        }
    }

    private void startTimer() {
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(16, _ -> {
            if (!gameOver && !isPaused) update();
            repaint();
        });
        gameTimer.start();
    }

    private void update() {
        handleInput();
        updateItemEffect();

        // 敌人生存计数
        int aliveCount = 0;
        Iterator<Bot> botIter = enemies.iterator();
        while (botIter.hasNext()) {
            Bot e = botIter.next();
            if (!e.alive) {
                botIter.remove();
                remainingEnemies--; // 杀一个总数减1
                score += 100;
                continue;
            }
            aliveCount++;
            e.autoMove(walls, enemies, player);
            e.autoShoot(bullets);
        }

        // BOSS移动
        if (boss != null && boss.hp > 0) {
            boss.autoMove(walls, player, enemies);
            boss.autoShoot(bullets);
        }

        updateBullets();
        checkCollisions();

        // 清完一波 -> 下一波
        if (aliveCount == 0 && boss == null) {
            boolean isFinalWave = mode == 0 && wave >= 3;
            if (mode == 1 && wave >= 4) isFinalWave = true;
            if (mode == 2 && wave >= 5) isFinalWave = true;

            if (!isFinalWave) {
                spawnWave();
            }
        }

        // 胜利（BOSS死了就赢）
        if (boss != null && boss.hp <= 0) {
            gameOver = true;
            showEndMenu = true;
            soundManager.stopBackgroundMusic();
        }

        // 失败
        if (!player.alive && player.life <= 0) {
            gameOver = true;
            showEndMenu = true;
            soundManager.stopBackgroundMusic();
        }
    }

    private void updateItemEffect() {
        if (speedTime > 0) speedTime--;
        if (doubleBulletTime > 0) doubleBulletTime--;
        if (shieldTime > 0) shieldTime--;
    }

    private void handleInput() {
        if (!player.alive) return;

        int baseSpeed = (speedTime > 0) ? 6 : 4;
        int dx = 0, dy = 0;

        if (keys[KeyEvent.VK_UP]) {
            dy = -baseSpeed;
            player.dir = 0;
        } else if (keys[KeyEvent.VK_DOWN]) {
            dy = baseSpeed;
            player.dir = 2;
        }
        if (keys[KeyEvent.VK_LEFT]) {
            dx = -baseSpeed;
            player.dir = 3;
        } else if (keys[KeyEvent.VK_RIGHT]) {
            dx = baseSpeed;
            player.dir = 1;
        }

        if (dx != 0) {
            int nx = player.x + dx;
            Rectangle r = new Rectangle(nx, player.y, 40, 40);
            if (isColliding(r)) player.x = nx;
        }
        if (dy != 0) {
            int ny = player.y + dy;
            Rectangle r = new Rectangle(player.x, ny, 40, 40);
            if (isColliding(r)) player.y = ny;
        }

        if (keys[KeyEvent.VK_SPACE] && player.canShoot()) {
            Bullet main = player.shoot();
            if (main != null) {
                bullets.add(main);
                soundManager.playFireSound();

                if (doubleBulletTime > 0) {
                    Bullet extra = new Bullet(player.x, player.y, player.dir, true);
                    if (player.dir == 0 || player.dir == 2) {
                        main.x -= 12;
                        extra.x += 12;
                    } else {
                        main.y -= 12;
                        extra.y += 12;
                    }
                    bullets.add(extra);
                }
            }
        }
    }

    private boolean isColliding(Rectangle rect) {
        if (rect.x < 20 || rect.x + rect.width > W - 20 ||
                rect.y < 20 || rect.y + rect.height > H - 20)
            return false;

        for (Wall w : walls) {
            if (w.broken || w.isGrass) continue;
            if (rect.intersects(w.getRect())) return false;
        }
        for (Bot bot : enemies) {
            if (bot.alive && rect.intersects(bot.getRect())) return false;
        }
        return boss == null || boss.hp <= 0 || !rect.intersects(boss.getRect());
    }

    private void updateBullets() {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            b.move();
            if (!b.alive) iter.remove();
        }
    }

    private void checkCollisions() {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            boolean hit = false;

            for (Wall w : walls) {
                if (w.isGrass || w.broken) continue;
                if (b.getRect().intersects(w.getRect())) {
                    w.hit();
                    hit = true;
                    break;
                }
            }
            if (hit) {
                iter.remove();
                continue;
            }

            if (b.isPlayer) {
                for (Bot e : enemies) {
                    if (e.alive && b.getRect().intersects(e.getRect())) {
                        e.alive = false;
                        hit = true;
                        break;
                    }
                }
                if (!hit && boss != null && boss.hp > 0 && b.getRect().intersects(boss.getRect())) {
                    boss.hp--;
                    // BOSS也算一个敌人
                    if (boss.hp <= 0) remainingEnemies--;
                    hit = true;
                }
            } else {
                if (player.alive && b.getRect().intersects(player.getRect())) {
                    if (shieldTime <= 0) {
                        player.hurt(1);
                    }
                    hit = true;
                }
            }

            if (hit) iter.remove();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();
        double scaleX = (double) panelW / W;
        double scaleY = (double) panelH / H;
        double scale = Math.min(scaleX, scaleY);
        int offsetX = (int) ((panelW - W * scale) / 2);
        int offsetY = (int) ((panelH - H * scale) / 2);

        for (Wall w : walls) {
            if (w.broken || w.isGrass) continue;
            int sx = (int) (w.x * scale) + offsetX;
            int sy = (int) (w.y * scale) + offsetY;
            int sw = (int) (w.w * scale);
            int sh = (int) (w.h * scale);
            if (w.canBreak) {
                g2d.setColor(new Color(180, 90, 40));
                g2d.fillRect(sx, sy, sw, sh);
                g2d.setColor(new Color(120, 50, 20));
                g2d.drawRect(sx, sy, sw, sh);
            } else {
                g2d.setColor(new Color(170,170,180));
                g2d.fillRect(sx, sy, sw, sh);
                g2d.setColor(new Color(110,110,120));
                g2d.drawRect(sx, sy, sw, sh);
            }
        }

        if (player.alive) {
            if (shieldTime > 0 && shieldTime / 5 % 2 == 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
            drawTank(g2d, player.x, player.y, player.dir, new Color(50,180,50), true, scale, offsetX, offsetY);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        for (Bot e : enemies) {
            if (!e.alive) continue;
            drawTank(g2d, e.x, e.y, e.dir, new Color(200,50,50), false, scale, offsetX, offsetY);
        }

        if (boss != null && boss.hp > 0) {
            drawBossTank(g2d, boss.x, boss.y, boss.dir, scale, offsetX, offsetY);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("黑体", Font.BOLD, (int)(16*scale)));
            g2d.drawString("BOSS HP:" + boss.hp, (int)(boss.x*scale)+offsetX, (int)(boss.y*scale)+offsetY-5);
        }

        for (Wall w : walls) {
            if (!w.isGrass) continue;
            int sx = (int)(w.x*scale)+offsetX;
            int sy = (int)(w.y*scale)+offsetY;
            int sw = (int)(w.w*scale);
            int sh = (int)(w.h*scale);
            g2d.setColor(new Color(30,160,60,160));
            g2d.fillRect(sx, sy, sw, sh);
        }

        for (Bullet b : bullets) {
            if (!b.alive) continue;
            int sx = (int)(b.x*scale)+offsetX;
            int sy = (int)(b.y*scale)+offsetY;
            int s = (int)(8*scale);
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(sx, sy, s, s);
        }

        // 界面显示：分数、模式、剩余敌人（核心修改）
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("黑体", Font.BOLD, (int)(20*scale)));
        g2d.drawString("分数: " + score, (int)(20*scale)+offsetX, (int)(30*scale)+offsetY);
        String[] site = {"简单","普通","困难"};
        g2d.drawString("模式: " + site[mode], (int)(550*scale)+offsetX, (int)(30*scale)+offsetY);
        g2d.drawString("剩余敌人: " + remainingEnemies, (int)(250*scale)+offsetX, (int)(30*scale)+offsetY);
        g2d.drawString("按B打开商城", (int)(320*scale)+offsetX, (int)(55*scale)+offsetY);

        int tipY = 80;
        if (speedTime > 0) {
            g2d.setColor(Color.CYAN);
            g2d.drawString("加速中",offsetX+20,offsetY+(int)(tipY*scale));
            tipY += 20;
        }
        if (doubleBulletTime > 0) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("双弹中",offsetX+20,offsetY+(int)(tipY*scale));
            tipY += 20;
        }
        if (shieldTime > 0) {
            g2d.setColor(Color.MAGENTA);
            g2d.drawString("无敌中",offsetX+20,offsetY+(int)(tipY*scale));
        }

        int barX = (int)(20*scale)+offsetX;
        int barY = (int)(tipY*scale)+offsetY+10;
        int barW = (int)(150*scale);
        int barH = (int)(12*scale);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barW, barH);
        double hpRate = (double)player.hp / player.maxHp;
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, (int)(barW*hpRate), barH);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barW, barH);
        g2d.drawString("生命: " + player.life, barX, barY-8);

        if (isPaused && !showShop) {
            g2d.setFont(new Font("黑体", Font.BOLD, (int)(50*scale)));
            g2d.setColor(Color.YELLOW);
            g2d.drawString("游戏暂停",offsetX+(int)(W*scale)/2-100,offsetY+(int)(H*scale)/2);
        }

        if (showShop) {
            g2d.setColor(new Color(0,0,0,180));
            g2d.fillRect(offsetX+150,offsetY+100,(int)(500*scale),(int)(350*scale));
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("黑体", Font.BOLD, (int)(30*scale)));
            g2d.drawString("道具商城（按1-4购买，B关闭）", offsetX+180, offsetY+150);
            g2d.drawString("当前分数: " + score, offsetX+180, offsetY+190);
            int yy = 230;
            for (int i=0;i<shopNames.length;i++) {
                g2d.drawString((i+1)+". "+shopNames[i], offsetX+180, offsetY+(int)(yy*scale));
                yy += 30;
            }
        }

        if (showEndMenu) {
            g2d.setFont(new Font("黑体", Font.BOLD, (int)(40*scale)));
            if (boss != null && boss.hp <= 0) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("胜利！",offsetX+(int)(W*scale)/2-80,offsetY+(int)(H*scale)/2-60);
            } else {
                g2d.setColor(Color.RED);
                g2d.drawString("游戏结束！",offsetX+(int)(W*scale)/2-100,offsetY+(int)(H*scale)/2-60);
            }
            g2d.setFont(new Font("黑体", Font.PLAIN, (int)(26*scale)));
            g2d.setColor(Color.WHITE);
            g2d.drawString("按1→重开本局",offsetX+(int)(W*scale)/2-60,offsetY+(int)(H*scale)/2);
            g2d.drawString("按2→返回选难度",offsetX+(int)(W*scale)/2-70,offsetY+(int)(H*scale)/2+40);
            g2d.drawString("按3→退出游戏",offsetX+(int)(W*scale)/2-60,offsetY+(int)(H*scale)/2+80);
        }
    }

    private void drawTank(Graphics2D g2d, int x, int y, int dir, Color bodyColor, boolean isPlayer,
                          double scale, int offsetX, int offsetY) {
        int sx = (int)(x * scale) + offsetX;
        int sy = (int)(y * scale) + offsetY;
        int size = (int)(40 * scale);
        int cx = sx + size/2;
        int cy = sy + size/2;

        g2d.setColor(new Color(40, 40, 40));
        int trackW = (int)(8 * scale);
        if (dir == 0 || dir == 2) {
            g2d.fillRect(sx, sy, trackW, size);
            g2d.fillRect(sx + size - trackW, sy, trackW, size);
            g2d.setColor(new Color(80, 80, 80));
            for (int i=0;i<3;i++) {
                g2d.fillRect(sx, sy + (int)(10*scale*i), trackW, (int)(3*scale));
                g2d.fillRect(sx + size - trackW, sy + (int)(10*scale*i), trackW, (int)(3*scale));
            }
        } else {
            g2d.fillRect(sx, sy, size, trackW);
            g2d.fillRect(sx, sy + size - trackW, size, trackW);
            g2d.setColor(new Color(80, 80, 80));
            for (int i=0;i<3;i++) {
                g2d.fillRect(sx + (int)(10*scale*i), sy, (int)(3*scale), trackW);
                g2d.fillRect(sx + (int)(10*scale*i), sy + size - trackW, (int)(3*scale), trackW);
            }
        }

        g2d.setColor(bodyColor);
        int bodyPad = (int)(10 * scale);
        g2d.fillRect(sx + bodyPad, sy + bodyPad, size - 2*bodyPad, size - 2*bodyPad);
        g2d.setColor(new Color(Math.min(255,bodyColor.getRed()+40),
                Math.min(255,bodyColor.getGreen()+40),
                Math.min(255,bodyColor.getBlue()+40), 180));
        g2d.fillRect(sx + bodyPad + (int)(2*scale), sy + bodyPad + (int)(2*scale), (int)(6*scale), (int)(6*scale));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(sx + bodyPad, sy + bodyPad, size - 2*bodyPad, size - 2*bodyPad);

        Color gunColor = isPlayer ? new Color(220, 60, 60) : new Color(60, 200, 60);
        g2d.setColor(gunColor);
        int gunW = (int)(6 * scale);
        int gunL = (int)(22 * scale);
        switch(dir) {
            case 0 -> g2d.fillRect(cx - gunW/2, sy, gunW, gunL);
            case 1 -> g2d.fillRect(sx + size - gunL, cy - gunW/2, gunL, gunW);
            case 2 -> g2d.fillRect(cx - gunW/2, sy + size - gunL, gunW, gunL);
            case 3 -> g2d.fillRect(sx, cy - gunW/2, gunL, gunW);
        }
        g2d.setColor(Color.BLACK);
        switch(dir) {
            case 0 -> g2d.drawRect(cx - gunW/2, sy, gunW, gunL);
            case 1 -> g2d.drawRect(sx + size - gunL, cy - gunW/2, gunL, gunW);
            case 2 -> g2d.drawRect(cx - gunW/2, sy + size - gunL, gunW, gunL);
            case 3 -> g2d.drawRect(sx, cy - gunW/2, gunL, gunW);
        }
    }

    private void drawBossTank(Graphics2D g2d, int x, int y, int dir, double scale, int offsetX, int offsetY) {
        int sx = (int)(x * scale) + offsetX;
        int sy = (int)(y * scale) + offsetY;
        int size = (int)(60 * scale);
        int cx = sx + size/2;
        int cy = sy + size/2;

        g2d.setColor(new Color(30, 30, 30));
        int trackW = (int)(10 * scale);
        if (dir == 0 || dir == 2) {
            g2d.fillRect(sx, sy, trackW, size);
            g2d.fillRect(sx + size - trackW, sy, trackW, size);
            g2d.setColor(new Color(70, 70, 70));
            for (int i=0;i<4;i++) {
                g2d.fillRect(sx, sy + (int)(12*scale*i), trackW, (int)(3*scale));
                g2d.fillRect(sx + size - trackW, sy + (int)(12*scale*i), trackW, (int)(3*scale));
            }
        } else {
            g2d.fillRect(sx, sy, size, trackW);
            g2d.fillRect(sx, sy + size - trackW, size, trackW);
            g2d.setColor(new Color(70, 70, 70));
            for (int i=0;i<4;i++) {
                g2d.fillRect(sx + (int)(12*scale*i), sy, (int)(3*scale), trackW);
                g2d.fillRect(sx + (int)(12*scale*i), sy + size - trackW, (int)(3*scale), trackW);
            }
        }

        g2d.setColor(new Color(160, 0, 0));
        int bodyPad = (int)(14 * scale);
        g2d.fillRect(sx + bodyPad, sy + bodyPad, size - 2*bodyPad, size - 2*bodyPad);
        g2d.setColor(new Color(220, 60, 60, 180));
        g2d.fillRect(sx + bodyPad + (int)(3*scale), sy + bodyPad + (int)(3*scale), (int)(8*scale), (int)(8*scale));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(sx + bodyPad, sy + bodyPad, size - 2*bodyPad, size - 2*bodyPad);

        g2d.setColor(new Color(100, 0, 0));
        int gunW = (int)(8 * scale);
        int gunL = (int)(30 * scale);
        switch(dir) {
            case 0 -> g2d.fillRect(cx - gunW/2, sy, gunW, gunL);
            case 1 -> g2d.fillRect(sx + size - gunL, cy - gunW/2, gunL, gunW);
            case 2 -> g2d.fillRect(cx - gunW/2, sy + size - gunL, gunW, gunL);
            case 3 -> g2d.fillRect(sx, cy - gunW/2, gunL, gunW);
        }
        g2d.setColor(Color.BLACK);
        switch(dir) {
            case 0 -> g2d.drawRect(cx - gunW/2, sy, gunW, gunL);
            case 1 -> g2d.drawRect(sx + size - gunL, cy - gunW/2, gunL, gunW);
            case 2 -> g2d.drawRect(cx - gunW/2, sy + size - gunL, gunW, gunL);
            case 3 -> g2d.drawRect(sx, cy - gunW/2, gunL, gunW);
        }
    }
}