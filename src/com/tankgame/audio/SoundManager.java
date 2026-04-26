package com.tankgame.audio;

public class SoundManager {
    private static SoundManager instance;
    private final AudioPlayer bgm;
    private final AudioPlayer fire;

    private SoundManager() {
        // 直接指向你项目里的 sounds 文件夹下的文件
        bgm = new AudioPlayer("sounds/bj.wav");
        fire = new AudioPlayer("sounds/fs.wav");
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic() {
        bgm.loop();
    }

    public void stopBackgroundMusic() {
        bgm.stop();
    }

    public void playFireSound() {
        fire.play();
    }

}