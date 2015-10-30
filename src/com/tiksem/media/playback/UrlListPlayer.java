package com.tiksem.media.playback;

import android.media.MediaPlayer;

import java.util.List;

/**
 * Created by stykhonenko on 23.10.15.
 */
public class UrlListPlayer extends Player {
    private List<String> playList;

    public UrlListPlayer(MediaPlayer mediaPlayer, List<String> playList) {
        super(mediaPlayer);
        this.playList = playList;
    }

    @Override
    protected void getCurrentUrl(OnUrlReady onUrlReady) {
        onUrlReady.onUrlReady(playList.get(getPosition()));
    }

    @Override
    protected int getPlayListSize() {
        return playList.size();
    }

    public void changePlayList(List<String> newPlayList) {
        int position = getPosition();
        if (playList == null) {
            throw new IllegalStateException("Unable to change playlist, no playlist set. Call play before");
        }

        String currentPlayingUrl = playList.get(position);
        int newPosition = newPlayList.indexOf(currentPlayingUrl);
        if (newPosition < 0) {
            throw new IllegalArgumentException("new playList should contain current playing url");
        }

        setPosition(newPosition);
        playList = newPlayList;
    }

    public List<String> getPlayList() {
        return playList;
    }

    @Override
    public String getCurrentUrl() {
        return playList.get(getPosition());
    }
}
