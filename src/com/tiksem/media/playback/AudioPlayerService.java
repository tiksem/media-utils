package com.tiksem.media.playback;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.utils.framework.collections.ListWithSelectedItem;
import com.utilsframework.android.Services;
import com.utilsframework.android.media.MediaPlayerProgressUpdater;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Tikhonenko.S
 * Date: 17.07.14
 * Time: 22:19
 */
public class AudioPlayerService extends Service {
    private MediaPlayer mediaPlayer;
    private Status status = Status.IDLE;
    private List<String> playList;
    private int position;
    private MediaPlayerProgressUpdater mediaPlayerProgressUpdater;
    private Set<PositionChangedListener> positionChangedListeners = new LinkedHashSet<>();
    private Set<PlaybackErrorListener> errorListeners = new LinkedHashSet<>();
    private Set<StateChangedListener> stateListeners = new LinkedHashSet<>();
    private Set<ProgressChangedListener> progressChangedListeners;

    void play(List<String> urls, int position) {
        if (urls == null) {
            throw new NullPointerException();
        }

        playList = urls;
        this.position = position;

        tryPlayCurrentUrl();
    }

    private void onPositionChanged() {
        for (PositionChangedListener listener : positionChangedListeners) {
            listener.onPositionChanged();
        }
    }

    private void goNext() {
        int size = playList.size();
        if (size >= 2) {
            if (position < size - 1) {
                position++;
            } else {
                position = 0;
            }

            onPositionChanged();
        }
    }

    private void goPrev() {
        int size = playList.size();
        if (size >= 2) {
            if (position == 0) {
                position = size - 1;
            } else {
                position--;
            }

            onPositionChanged();
        }
    }

    private void onError(String url) {
        for (PlaybackErrorListener listener : errorListeners) {
            listener.onError(url);
        }
    }

    private void tryPlayCurrentUrl() {
        final String url = playList.get(position);
        try {
            setStatus(Status.PREPARING);
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

    private void onPlayingUrlError(String url) {
        onError(url);
        goNext();
        tryPlayCurrentUrl();
    }

    void changePlayList(List<String> newPlayListUrls) {
        if (playList == null) {
            throw new IllegalStateException("Unable to change playlist, no playlist set. Call play before");
        }

        String currentPlayingUrl = playList.get(position);
        int newPosition = newPlayListUrls.indexOf(currentPlayingUrl);
        if (newPosition < 0) {
            throw new IllegalArgumentException("new playList should contain current playing url");
        }

        position = newPosition;
        onPositionChanged();
        playList = newPlayListUrls;
    }

    void pause() {
        if (status != Status.PLAYING) {
            throw new IllegalStateException("pause can be called only in PLAYING state");
        }

        mediaPlayer.pause();
        setStatus(Status.PAUSED);
    }

    void resume() {
        if (status != Status.PAUSED) {
            throw new IllegalStateException("resume can be called only in PAUSED state");
        }

        mediaPlayer.start();
        setStatus(Status.PLAYING);
    }

    void togglePauseState() {
        if (status == Status.PLAYING) {
            pause();
        } else if(status == Status.PAUSED) {
            resume();
        } else {
            throw new IllegalStateException("togglePauseState can be called only in PAUSED or PLAYING state");
        }
    }

    void playNext() {
        goNext();
        tryPlayCurrentUrl();
    }

    void playPrev() {
        goPrev();
        tryPlayCurrentUrl();
    }

    boolean isPaused() {
        return status == Status.PAUSED;
    }
    
    boolean isPlaying() {
        return status == Status.PLAYING;
    }

    void addProgressChangedListener(ProgressChangedListener listener) {
        if (progressChangedListeners == null) {
            progressChangedListeners = new LinkedHashSet<>();
            mediaPlayerProgressUpdater = new ProgressUpdater();
            mediaPlayerProgressUpdater.setMediaPlayer(mediaPlayer);
        }
        
        progressChangedListeners.add(listener);
    }
    
    void removeProgressChangedListener(ProgressChangedListener listener) {
        if (progressChangedListeners != null) {
            progressChangedListeners.remove(listener);
            destroyProgressUpdaterIfEmptyListeners();
        }
    }

    private void destroyProgressUpdaterIfEmptyListeners() {
        if (progressChangedListeners.isEmpty()) {
            progressChangedListeners = null;
            mediaPlayerProgressUpdater.destroy();
            mediaPlayerProgressUpdater = null;
        }
    }

    void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    private void setStatus(Status status) {
        this.status = status;

        for (StateChangedListener listener : stateListeners) {
            listener.onStateChanged(status);
        }
    }

    public class Binder extends android.os.Binder implements Services.OnUnbind {
        private Set<PositionChangedListener> currentPositionChangedListeners = new HashSet<>();
        private Set<PlaybackErrorListener> currentErrorListeners = new HashSet<>();
        private Set<ProgressChangedListener> currentProgressChangedListeners = new HashSet<>();
        private Set<StateChangedListener> currentStateListeners = new HashSet<>();

        public void play(List<String> urls, int position) {
            AudioPlayerService.this.play(urls, position);
        }

        public void play(int position) {
            play(playList, position);
        }

        public void play(List<String> urls) {
            play(urls, 0);
        }

        public void changePlayList(List<String> newPlayListUrls) {
            AudioPlayerService.this.changePlayList(newPlayListUrls);
        }

        public void pause() {
            AudioPlayerService.this.pause();
        }

        public void resume() {
            AudioPlayerService.this.resume();
        }

        public void togglePauseState() {
            AudioPlayerService.this.togglePauseState();
        }

        public boolean isPaused() {
            return AudioPlayerService.this.isPaused();
        }

        public boolean isPlaying() {
            return AudioPlayerService.this.isPlaying();
        }

        public Status getStatus() {
            return status;
        }

        public void playNext() {
            AudioPlayerService.this.playNext();
        }

        public void playPrev() {
            AudioPlayerService.this.playPrev();
        }

        public void addPositionChangedListener(PositionChangedListener listener) {
            currentPositionChangedListeners.add(listener);
            positionChangedListeners.add(listener);
        }

        public void removePositionChangedListener(PositionChangedListener listener) {
            currentPositionChangedListeners.remove(listener);
            positionChangedListeners.remove(listener);
        }

        public void addPlayBackErrorListener(PlaybackErrorListener listener) {
            currentErrorListeners.add(listener);
            errorListeners.add(listener);
        }

        public void removePlayBackErrorListener(PlaybackErrorListener listener) {
            currentErrorListeners.remove(listener);
            errorListeners.remove(listener);
        }

        public void addStateChangedListener(StateChangedListener listener) {
            currentStateListeners.add(listener);
            stateListeners.add(listener);
        }

        public void removeStateChangedListener(StateChangedListener listener) {
            currentStateListeners.remove(listener);
            stateListeners.remove(listener);
        }

        public void addProgressChangedListener(ProgressChangedListener listener) {
            AudioPlayerService.this.addProgressChangedListener(listener);
            currentProgressChangedListeners.add(listener);
        }

        public void removeProgressChangedListener(ProgressChangedListener listener) {
            AudioPlayerService.this.removeProgressChangedListener(listener);
            currentProgressChangedListeners.remove(listener);
        }
        
        public void onUnbind() {
            positionChangedListeners.removeAll(currentPositionChangedListeners);
            errorListeners.removeAll(currentErrorListeners);
            stateListeners.removeAll(currentStateListeners);

            if (progressChangedListeners != null) {
                progressChangedListeners.removeAll(currentProgressChangedListeners);
                destroyProgressUpdaterIfEmptyListeners();
            }
        }

        public void seekTo(int msec) {
            AudioPlayerService.this.seekTo(msec);
        }

        public int getDuration() {
            return AudioPlayerService.this.getDuration();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        
        if (mediaPlayerProgressUpdater != null) {
            mediaPlayerProgressUpdater.destroy();
        }
    }

    public static void bind(Context context, Services.OnBind<Binder> onBind) {
        Services.start(context, AudioPlayerService.class);
        Services.bind(context, AudioPlayerService.class, onBind);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    private class ProgressUpdater extends MediaPlayerProgressUpdater {
        @Override
        protected void onProgressChanged(long progress, long max) {
            for (ProgressChangedListener listener : progressChangedListeners) {
                listener.onProgressChanged(progress, max);
            }
        }
    }
}
