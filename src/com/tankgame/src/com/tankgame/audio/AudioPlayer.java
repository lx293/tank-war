package com.tankgame.audio;

import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {
    private Clip clip;

    public AudioPlayer(String path, boolean loop) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            clip = AudioSystem.getClip();
            clip.open(ais);
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception ignored) {}
    }

    public void play() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop() {
        if (clip != null) clip.stop();
    }

    public void close() {
        if (clip != null) clip.close();
    }
}