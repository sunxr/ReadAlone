package com.github.baby.owspace.player;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player implements IPlayback, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    private static volatile Player sInstance;
    private MediaPlayer mediaPlayer;
    private List<Callback> mCallbacks = new ArrayList<>();
    private boolean isPaused;
    private String song;

    private Player() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
    }

    public static Player getInstance() {
        if (sInstance == null) {
            synchronized (Player.class) {
                if (sInstance == null) {
                    sInstance = new Player();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        notifyPlayStatusChanged(PlayState.COMPLETE);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        notifyPlayStatusChanged(PlayState.ERROR);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        notifyPlayStatusChanged(PlayState.PLAYING);
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mediaPlayer.start();
            notifyPlayStatusChanged(PlayState.PLAYING);
            return true;
        }
        return false;
    }

    @Override
    public boolean play(String song) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song);
            mediaPlayer.prepare();
            this.song = song;
            notifyPlayStatusChanged(PlayState.PLAYING);
            return true;
        } catch (IOException e) {
            notifyPlayStatusChanged(PlayState.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            notifyPlayStatusChanged(PlayState.PAUSE);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean seekTo(int progress) {
        try {
            mediaPlayer.seekTo(progress);
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void removeCallbacks() {
        mCallbacks.clear();
    }

    @Override
    public void releasePlayer() {
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        sInstance = null;
        song = null;
    }

    public String getSong() {
        return song;
    }

    private void notifyPlayStatusChanged(PlayState state) {
        for (Callback callback: mCallbacks) {
            callback.onPlayStatusChanged(state);
        }
    }

    private void notifyComplete(PlayState state) {
        for (Callback callback : mCallbacks) {
            callback.onComplete(state);
        }
    }
}
