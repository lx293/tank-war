package com.tankgame.audio;

public class SoundManager {
    private static SoundManager instance;
    private AudioPlayer bgm, fire;

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

    public void releaseAll() {
        bgm.close();
        fire.close();
    }
}