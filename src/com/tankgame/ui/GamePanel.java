package com.tankgame.ui;

import com.tankgame.Main;
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
    List<Item> items = new ArrayList<>();
    private boolean[] keys = new boolean[256];
    boolean gameOver = false, isPaused = false, showEndMenu = false;
    int score = 0;
    private Timer gameTimer;
    private JDialog modeDialog;
    private SoundManager soundManager;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();
        setDoubleBuffered(true);
        loadModeImage();
        setKeyBindings();
        soundManager = SoundManager.getInstance();
        showModeDialog();
    }

    private void setKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "toggleFull");
        getActionMap().put("toggleFull", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.toggleFullScreen();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "toggleSize");
        getActionMap().put("toggleSize", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.toggleWindowSize();
            }
        });
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
        JButton hard = new JButton("困难");
        easy.setBounds(320, 200, 160, 50);
        normal.setBounds(320, 280, 160, 50);
        hard.setBounds(320, 360, 160, 50);
        easy.setFont(new Font("黑体", Font.BOLD, 22));
        normal.setFont(new Font("黑体", Font.BOLD, 22));
        hard.setFont(new Font("黑体", Font.BOLD, 22));

        easy.addActionListener(e -> { mode = 0; modeDialog.dispose(); startGame(); });
        normal.addActionListener(e -> { mode = 1; modeDialog.dispose(); startGame(); });
        hard.addActionListener(e -> { mode = 2; modeDialog.dispose(); startGame(); });

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
        score = 0;
        enemies.clear();
        walls.clear();
        bullets.clear();
        items.clear();

        player = new PlayerOne(W/2-20, H-60);
        initEnemies();
        initMap();
        initKeyListener();
        startTimer();
        soundManager.playBackgroundMusic();
    }

    private void initEnemies() {
        enemies.clear();
        if (mode == 0) {
            enemies.add(new Bot(100, 100));
            enemies.add(new Bot(660, 100));
        }
        if (mode == 1) {
            enemies.add(new Bot(100, 100));
            enemies.add(new Bot(400, 100));
            enemies.add(new Bot(660, 100));
        }
        if (mode == 2) {
            enemies.add(new Bot(80, 80));
            enemies.add(new Bot(240, 80));
            enemies.add(new Bot(400, 80));
            enemies.add(new Bot(560, 80));
            enemies.add(new Bot(720, 80));
        }
    }

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
            addRect(260, 180, 4, 4, true);
            addRect(500, 180, 4, 4, true);
        }
        if (mode == 1) {
            addRect(200, 100, 4, 8, true);
            addRect(560, 100, 4, 8, true);
            addRect(380, 160, 4, 4, false);
        }
        if (mode == 2) {
            addRect(160, 80, 3, 12, true);
            addRect(610, 80, 3, 12, true);
            addRect(370, 140, 6, 6, false);
        }

        addRect(320, 480, 8, 1, true);
        walls.removeIf(w -> w.x == 400 && w.y == 480);
        addRect(320, 500, 1, 4, true);
        addRect(460, 500, 1, 4, true);
    }

    private void addRect(int x, int y, int w, int h, boolean c) {
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                walls.add(new Wall(x + i * TILE, y + j * TILE, TILE, TILE, c));
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
                if (e.getKeyCode() < keys.length) {
                    keys[e.getKeyCode()] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_P) isPaused = !isPaused;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() < keys.length) {
                    keys[e.getKeyCode()] = false;
                }
            }
        });
    }

    private void startTimer() {
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(16, e -> {
            if (!gameOver && !isPaused) update();
            repaint();
        });
        gameTimer.start();
    }

    private void update() {
        handleInput();
        player.updateBullets(bullets);

        Iterator<Bot> botIter = enemies.iterator();
        while (botIter.hasNext()) {
            Bot e = botIter.next();
            if (!e.alive) {
                botIter.remove();
                continue;
            }
            e.autoMove(walls);
            e.autoShoot(bullets);
            e.updateBullets(bullets);
        }

        updateBullets();
        checkCollisions();
        checkItem();

        if (enemies.isEmpty()) {
            gameOver = true;
            showEndMenu = true;
            soundManager.stopBackgroundMusic();
        } else if (!player.alive && player.life <= 0) {
            gameOver = true;
            showEndMenu = true;
            soundManager.stopBackgroundMusic();
        }
    }

    private void handleInput() {
        int dx = 0, dy = 0;
        if (keys[KeyEvent.VK_UP]) {
            dy = -player.speed;
            player.dir = 0;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            dy = player.speed;
            player.dir = 2;
        }
        if (keys[KeyEvent.VK_LEFT]) {
            dx = -player.speed;
            player.dir = 3;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            dx = player.speed;
            player.dir = 1;
        }
        player.move(dx, dy, walls);

        if (keys[KeyEvent.VK_SPACE] && player.canShoot()) {
            Bullet b = player.shoot();
            if (b != null) {
                bullets.add(b);
                soundManager.playFireSound();
            }
        }
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
                if (!w.broken && b.getRect().intersects(w.getRect())) {
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
                Iterator<Bot> eIter = enemies.iterator();
                while (eIter.hasNext()) {
                    Bot e = eIter.next();
                    if (e.alive && b.getRect().intersects(e.getRect())) {
                        e.alive = false;
                        score += 100;
                        hit = true;
                        break;
                    }
                }
            } else {
                if (player.alive && b.getRect().intersects(player.getRect())) {
                    player.hurt(1);
                    hit = true;
                }
            }
            if (hit) iter.remove();
        }
    }

    private void checkItem() {
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item it = iter.next();
            if (it.alive && player.getRect().intersects(it.getRect())) {
                player.useItem(it.type);
                it.alive = false;
                iter.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int panelW = getWidth();
        int panelH = getHeight();
        double scaleX = (double) panelW / W;
        double scaleY = (double) panelH / H;
        double scale = Math.min(scaleX, scaleY);

        for (Wall w : walls) {
            if (w.broken) continue;
            int sx = (int) (w.x * scaleX);
            int sy = (int) (w.y * scaleY);
            int sw = (int) (w.w * scaleX);
            int sh = (int) (w.h * scaleY);

            if (w.canBreak) {
                g2d.setColor(new Color(160, 80, 0));
                g2d.fillRect(sx, sy, sw, sh);
                g2d.setColor(new Color(200, 100, 0));
                g2d.drawRect(sx, sy, sw, sh);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(sx, sy, sw, sh);
                g2d.setColor(Color.WHITE);
                g2d.drawRect(sx, sy, sw, sh);
            }
        }

        for (Item it : items) {
            if (!it.alive) continue;
            int sx = (int) (it.x * scaleX);
            int sy = (int) (it.y * scaleY);
            int s = (int) (20 * scale);
            g2d.setColor(Color.CYAN);
            g2d.fillOval(sx, sy, s, s);
        }

        if (player.alive) {
            int sx = (int) (player.x * scaleX);
            int sy = (int) (player.y * scaleY);
            int s = (int) (40 * scale);
            int cx = sx + s / 2;
            int cy = sy + s / 2;

            g2d.setColor(new Color(255, 215, 0));
            g2d.fillRoundRect(sx, sy, s, s, 4, 4);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(sx, sy, s, s, 4, 4);

            int bw = (int) (12 * scale);
            int bh = (int) (30 * scale);
            g2d.setColor(Color.GREEN);
            switch (player.dir) {
                case 0: g2d.fillRoundRect(cx - bw / 2, cy - bh, bw, bh, 2, 2); break;
                case 1: g2d.fillRoundRect(cx, cy - bw / 2, bh, bw, 2, 2); break;
                case 2: g2d.fillRoundRect(cx - bw / 2, cy, bw, bh, 2, 2); break;
                case 3: g2d.fillRoundRect(cx - bh, cy - bw / 2, bh, bw, 2, 2); break;
            }

            int tw = (int) (8 * scale);
            int ty = (int) (sy + 4 * scale);
            int th = (int) (s - 8 * scale);
            g2d.setColor(new Color(180, 140, 0));
            g2d.fillRoundRect(sx, ty, tw, th, 2, 2);
            g2d.fillRoundRect(sx + s - tw, ty, tw, th, 2, 2);
        }

        for (Bot e : enemies) {
            if (!e.alive) continue;
            int sx = (int) (e.x * scaleX);
            int sy = (int) (e.y * scaleY);
            int s = (int) (40 * scale);
            int cx = sx + s / 2;
            int cy = sy + s / 2;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(sx, sy, s, s, 4, 4);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(sx, sy, s, s, 4, 4);

            int bw = (int) (12 * scale);
            int bh = (int) (30 * scale);
            g2d.setColor(Color.GREEN);
            switch (e.dir) {
                case 0: g2d.fillRoundRect(cx - bw / 2, cy - bh, bw, bh, 2, 2); break;
                case 1: g2d.fillRoundRect(cx, cy - bw / 2, bh, bw, 2, 2); break;
                case 2: g2d.fillRoundRect(cx - bw / 2, cy, bw, bh, 2, 2); break;
                case 3: g2d.fillRoundRect(cx - bh, cy - bw / 2, bh, bw, 2, 2); break;
            }

            int tw = (int) (8 * scale);
            int ty = (int) (sy + 4 * scale);
            int th = (int) (s - 8 * scale);
            g2d.setColor(Color.GRAY);
            g2d.fillRoundRect(sx, ty, tw, th, 2, 2);
            g2d.fillRoundRect(sx + s - tw, ty, tw, th, 2, 2);
        }

        for (Bullet b : bullets) {
            if (!b.alive) continue;
            int sx = (int) (b.x * scaleX);
            int sy = (int) (b.y * scaleY);
            int s = (int) (8 * scale);
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(sx, sy, s, s);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("黑体", Font.BOLD, (int) (20 * scale)));
        g2d.drawString("分数: " + score, (int) (20 * scaleX), (int) (30 * scaleY));
        String modeStr = mode == 0 ? "简单" : mode == 1 ? "普通" : "困难";
        g2d.drawString("模式: " + modeStr, (int) (700 * scaleX), (int) (30 * scaleY));

        int barX = (int) (20 * scaleX);
        int barY = (int) (50 * scaleY);
        int barW = (int) (150 * scale);
        int barH = (int) (12 * scale);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barW, barH);
        double hpRate = (double) player.hp / player.maxHp;
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, (int) (barW * hpRate), barH);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barW, barH);

        g2d.drawString("生命: " + player.life, barX, barY - 8);

        if (isPaused) {
            g2d.setFont(new Font("黑体", Font.BOLD, (int) (50 * scale)));
            g2d.setColor(Color.YELLOW);
            g2d.drawString("游戏暂停", panelW / 2 - 100, panelH / 2);
        }

        if (showEndMenu) {
            g2d.setFont(new Font("黑体", Font.BOLD, (int) (40 * scale)));
            if (enemies.isEmpty()) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("胜利！所有敌人已消灭", panelW / 2 - 150, panelH / 2 - 60);
            } else {
                g2d.setColor(Color.RED);
                g2d.drawString("游戏结束！生命已耗尽", panelW / 2 - 150, panelH / 2 - 60);
            }

            g2d.setFont(new Font("黑体", Font.PLAIN, (int) (26 * scale)));
            g2d.setColor(Color.WHITE);
            g2d.drawString("按 1 → 重新开始本局", panelW / 2 - 80, panelH / 2);
            g2d.drawString("按 2 → 返回难度选择", panelW / 2 - 80, panelH / 2 + 40);
            g2d.drawString("按 3 → 退出游戏", panelW / 2 - 80, panelH / 2 + 80);
        }
    }
}