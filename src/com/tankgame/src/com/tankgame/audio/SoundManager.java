package com.tankgame.audio;

public class SoundManager {
    private static SoundManager instance;
    private final AudioPlayer bgm;
    private final AudioPlayer fire;

    private SoundManager() {
        bgm = new AudioPlayer("sounds/bj.wav", true);
        fire = new AudioPlayer("sounds/fs.wav", false);
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic() {
        bgm.play();
    }

    public void stopBackgroundMusic() {
        bgm.stop();
    }

    public void playFireSound() {
        fire.play();
    }

// --注释掉检查 START (2026/4/19 19:10):
//    public void releaseAll() {
//        bgm.close();
//        fire.close();
//    }
// --注释掉检查 STOP (2026/4/19 19:10)
}