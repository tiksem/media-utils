package com.tiksem.media.playback;

import android.media.MediaPlayer;

import java.util.List;

/**
 * Created by stykhonenko on 23.10.15.
 */
public class UrlListPlayer extends Player<String> {
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

    @Override
    public List<String> getPlayList() {
        return playList;
    }

    @Override
    public String getCurrentUrl() {
        return playList.get(getPosition());
    }

    @Override
    protected void setPlayList(List<String> newPlayList) {
        playList = newPlayList;
    }
}
