package com.tiksem.media.playback;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 23.10.15.
 */
public abstract class Player<PlayingEntity> {
    private MediaPlayer mediaPlayer;
    private Status status = Status.IDLE;
    private int position = -1;
    private Listener listener;

    public interface Listener {
        void onPositionChanged();
        void onStatusChanged();
        void onError(String url);
    }

    public Player(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Status getStatus() {
        return status;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public final void play(int position) {
        if (this.position == position) {
            return;
        }
        setPosition(position);
        tryPlayCurrentUrl();
    }

    protected interface OnUrlReady {
        void onUrlReady(String url);
    }

    protected abstract void getCurrentUrl(OnUrlReady onUrlReady);

    private void tryPlayCurrentUrl() {
        reset();
        setStatus(Status.PREPARING);
        getCurrentUrl(new OnUrlReady() {
            @Override
            public void onUrlReady(String url) {
                tryPlayUrl(url);
            }
        });
    }

    private void tryPlayUrl(final String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    onPlayingUrlError(url);
                    return true;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNext();
                }
            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    setStatus(Status.PLAYING);
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            onPlayingUrlError(url);
        }
    }

    protected void onPlayingUrlError(String url) {
        onError(url);

        if (canGoToNextUrl()) {
            goToNextUrl();
        } else {
            goNext();
        }
        tryPlayCurrentUrl();
    }

    protected void onError(String url) {
        if (listener != null) {
            listener.onError(url);
        }
    }

    public void playNext() {
        goNext();
        tryPlayCurrentUrl();
    }

    public void playPrev() {
        goPrev();
        tryPlayCurrentUrl();
    }

    protected void goToNextUrl() {
        throw new UnsupportedOperationException("Override this method if supportsSeveralUrlsForAudio returns true");
    }

    protected boolean canGoToNextUrl() {
        return false;
    }

    protected final void setPosition(int newPosition) {
        if (position == newPosition) {
            return;
        }

        position = newPosition;

        onPositionChanged();
    }

    protected void onPositionChanged() {
        if(listener != null) {
            listener.onPositionChanged();
        }
    }

    protected final int getPosition() {
        return position;
    }

    private void setStatus(Status status) {
        this.status = status;

        if (listener != null) {
            listener.onStatusChanged();
        }
    }

    private void goNext() {
        int size = getPlayListSize();
        if (size >= 2) {
            int position = getPosition();
            if (position < size - 1) {
                position++;
            } else {
                position = 0;
            }

            setPosition(position);
        }
    }

    private void goPrev() {
        int size = getPlayListSize();
        if (size >= 2) {
            int position = getPosition();
            if (position == 0) {
                position = size - 1;
            } else {
                position--;
            }

            setPosition(position);
        }
    }

    public void pause() {
        if (status != Status.PLAYING) {
            throw new IllegalStateException("pause can be called only in PLAYING state");
        }

        mediaPlayer.pause();
        setStatus(Status.PAUSED);
    }

    public void resume() {
        if (status != Status.PAUSED) {
            throw new IllegalStateException("resume can be called only in PAUSED state");
        }

        mediaPlayer.start();
        setStatus(Status.PLAYING);
    }

    public void togglePauseState() {
        if (status == Status.PLAYING) {
            pause();
        } else if(status == Status.PAUSED) {
            resume();
        } else {
            throw new IllegalStateException("togglePauseState can be called only in PAUSED or PLAYING state");
        }
    }

    public void changePlayList(List<PlayingEntity> newPlayList) {
        int position = getPosition();
        List<PlayingEntity> playList = getPlayList();
        if (playList == null) {
            throw new IllegalStateException("Unable to change playlist, no playlist set. Call play before");
        }

        PlayingEntity currentPlayingEntity = playList.get(position);
        int newPosition = newPlayList.indexOf(currentPlayingEntity);
        if (newPosition < 0) {
            throw new IllegalArgumentException("new playList should contain current playing url");
        }

        setPosition(newPosition);
        setPlayList(newPlayList);
    }

    public void reset() {
        mediaPlayer.reset();
    }

    protected abstract int getPlayListSize();

    public abstract String getCurrentUrl();

    public abstract List<PlayingEntity> getPlayList();
    protected abstract void setPlayList(List<PlayingEntity> newPlayList);
}
