package com.tiksem.media.playback;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.utilsframework.android.Services;
import com.utilsframework.android.media.MediaPlayerProgressUpdater;
import com.utilsframework.android.network.AsyncRequestExecutorManager;
import com.utilsframework.android.network.RequestManager;

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
public class AudioPlayerService extends Service implements Player.Listener {
    private MediaPlayer mediaPlayer;
    private MediaPlayerProgressUpdater mediaPlayerProgressUpdater;
    private Set<PositionChangedListener> positionChangedListeners = new LinkedHashSet<>();
    private Set<PlaybackErrorListener> errorListeners = new LinkedHashSet<>();
    private Set<StateChangedListener> stateListeners = new LinkedHashSet<>();
    private Set<ProgressChangedListener> progressChangedListeners;
    private UrlListPlayer urlListPlayer;
    private UrlsProviderListPlayer urlsProviderListPlayer;
    private RequestManager requestManager;

    private void setupPlayer(Player player) {
        player.setListener(this);
    }

    void play(List<String> urls, int position) {
        urlsProviderListPlayer = null;
        urlListPlayer = new UrlListPlayer(mediaPlayer, urls);
        setupPlayer(urlListPlayer);
        urlListPlayer.play(position);
    }

    void playUrlProviders(List<UrlsProvider> urlsProviders, int position) {
        if (urlsProviderListPlayer != null) {
            if (urlsProviderListPlayer.getPlayList() == urlsProviders) {
                urlsProviderListPlayer.play(position);
                return;
            } else {
                urlsProviderListPlayer.reset();
            }
        }

        urlListPlayer = null;

        if (requestManager == null) {
            requestManager = new AsyncRequestExecutorManager();
        }

        urlsProviderListPlayer = new UrlsProviderListPlayer(mediaPlayer, requestManager, urlsProviders);
        setupPlayer(urlsProviderListPlayer);
        urlsProviderListPlayer.play(position);
    }

    @Override
    public void onPositionChanged() {
        for (PositionChangedListener listener : positionChangedListeners) {
            listener.onPositionChanged();
        }
    }

    @Override
    public void onError(String url) {
        for (PlaybackErrorListener listener : errorListeners) {
            listener.onError(url);
        }
    }

    void changePlayList(List<String> newPlayListUrls) {
        if (urlListPlayer == null) {
            throw new IllegalStateException("Unable to call changePlayList, when playList is not set");
        }

        urlListPlayer.changePlayList(newPlayListUrls);
    }

    void changePlayListProviders(List<UrlsProvider> newPlayList) {
        if (urlsProviderListPlayer == null) {
            throw new IllegalStateException("Unable to call changePlayList, when playList is not set");
        }

        urlsProviderListPlayer.changePlayList(newPlayList);
    }

    Player getPlayer() {
        if (urlListPlayer != null) {
            return urlListPlayer;
        }

        return urlsProviderListPlayer;
    }

    Status getStatus() {
        Player player = getPlayer();
        if (player == null) {
            return Status.IDLE;
        }

        return player.getStatus();
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

    @Override
    public void onStatusChanged() {
        for (StateChangedListener listener : stateListeners) {
            listener.onStateChanged(getStatus());
        }
    }

    public void reset() {
        Player player = getPlayer();
        if (player != null) {
            urlsProviderListPlayer = null;
            urlListPlayer = null;
            player.reset();
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

        public void playUrlsProviders(List<UrlsProvider> urlsProviders, int position) {
            AudioPlayerService.this.playUrlProviders(urlsProviders, position);
        }

        public void play(int position) {
            getPlayer().play(position);
        }

        public void play(List<String> urls) {
            play(urls, 0);
        }

        public void changePlayList(List<String> newPlayListUrls) {
            AudioPlayerService.this.changePlayList(newPlayListUrls);
        }

        public void changePlayListProviders(List<UrlsProvider> newPlayList) {
            AudioPlayerService.this.changePlayListProviders(newPlayList);
        }

        public void pause() {
            Player player = getPlayer();
            if (player != null) {
                player.pause();
            } else {
                throw new IllegalStateException("pause is called on IDLE state");
            }
        }

        public void resume() {
            Player player = getPlayer();
            if (player != null) {
                player.resume();
            } else {
                throw new IllegalStateException("resume is called on IDLE state");
            }
        }

        public void togglePauseState() {
            Player player = getPlayer();
            if (player != null) {
                player.togglePauseState();
            } else {
                throw new IllegalStateException("togglePauseState is called on IDLE state");
            }
        }

        public boolean isPaused() {
            return getStatus() == Status.PAUSED;
        }

        public boolean isPlaying() {
            return getStatus() == Status.PLAYING;
        }

        public Status getStatus() {
            return AudioPlayerService.this.getStatus();
        }

        public void playNext() {
            Player player = getPlayer();
            if (player == null) {
                throw new IllegalStateException("set play list first");
            }

            player.playNext();
        }

        public void playPrev() {
            Player player = getPlayer();
            if (player == null) {
                throw new IllegalStateException("set play list first");
            }

            player.playPrev();
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

        public int getPosition() {
            Player player = getPlayer();
            if (player == null) {
                return -1;
            }

            return player.getPosition();
        }

        public List<String> getPlayList() {
            if (urlListPlayer == null) {
                return null;
            }

            return urlListPlayer.getPlayList();
        }

        public List<UrlsProvider> getUrlsProviders() {
            if (urlsProviderListPlayer == null) {
                return null;
            }

            return urlsProviderListPlayer.getProviders();
        }

        public int getProviderUrlPosition() {
            if (urlsProviderListPlayer == null) {
                return -1;
            }

            return urlsProviderListPlayer.getUrlPosition();
        }

        public String getCurrentUrl() {
            Player player = getPlayer();
            if (player == null) {
                throw new IllegalStateException("getCurrentUrl can only be called when player is selected");
            }

            return player.getCurrentUrl();
        }

        public void reset() {
            AudioPlayerService.this.reset();
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

        if (requestManager != null) {
            requestManager.cancelAll();
        }
    }

    public static void bindAndStart(Context context, Services.OnBind<Binder> onBind) {
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
