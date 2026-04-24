package com.tankgame;

import com.tankgame.ui.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Main {
    public static JFrame gameFrame;
    public static BufferedImage startImg;
    // --注释掉检查 (2026/4/19 19:10):public static boolean isFullScreen = false;

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                startImg = ImageIO.read(new File("images/cs.PNG"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            showStartUI();
        });
    }

    public static void showStartUI() {
        JFrame startFrame = new JFrame("坦克大战");
        startFrame.setSize(800, 600);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setResizable(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (startImg != null) {
                    g.drawImage(startImg, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        panel.setLayout(null);

        JButton startBtn = new JButton("开始游戏");
        startBtn.setBounds(320, 250, 160, 50);
        startBtn.setFont(new Font("黑体", Font.BOLD, 22));
        startBtn.addActionListener(e -> {
            startFrame.dispose();
            startGame();
        });
        panel.add(startBtn);
        startFrame.add(panel);
        startFrame.setVisible(true);
    }

    public static void startGame() {
        gameFrame = new JFrame("坦克大战");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(1280, 960);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);
        GamePanel panel = new GamePanel();
        gameFrame.add(panel);
        gameFrame.setVisible(true);
        panel.requestFocusInWindow();
    }

}