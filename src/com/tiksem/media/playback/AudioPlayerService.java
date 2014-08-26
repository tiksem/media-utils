package com.tiksem.media.playback;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.tiksem.media.data.Audio;
import com.utils.framework.Cancelable;
import com.utils.framework.CancelableUtils;
import com.utils.framework.collections.ListSelectedItemPositionManager;
import com.utils.framework.collections.ListWithSelectedItem;
import com.utils.framework.collections.SelectedItemPositionManager;
import com.utilsframework.android.Pauseable;
import com.utilsframework.android.Services;
import com.utilsframework.android.media.MediaPlayerProgressUpdater;
import com.utilsframework.android.view.UiMessages;

import java.io.IOException;
import java.util.*;

/**
 * User: Tikhonenko.S
 * Date: 17.07.14
 * Time: 22:19
 */
public class AudioPlayerService extends Service {
    private PlayerBinder binder = new PlayerBinder();
    private SelectedItemPositionManager<Audio> selectedItemPositionManager;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPaused = false;
    private AudioUrlsProvider audioUrlsProvider = new LocalAudioUrlsProvider();
    private Iterator<String> urlsIterator;
    private Object playListTag;

    private Queue<Cancelable> audioPlayingOperations = new ArrayDeque<Cancelable>();

    private Set<PlayBackListener> playBackListeners = new LinkedHashSet<PlayBackListener>();

    private MediaPlayerProgressUpdater mediaPlayerProgressUpdater = new MediaPlayerProgressUpdater() {
        @Override
        protected void onProgressChanged(long progress, long max) {
            callProgressChangedListeners(progress, max);
        }
    };

    public interface PlayBackListener {
        void onAudioPlayingStarted();
        void onAudioPlayingComplete();
        void onAudioPlayingPaused();
        void onAudioPlayingResumed();
        void onProgressChanged(long progress, long max);
    }

    private void onPlayAudioUrlFailed() {
        tryPlayNextAudioUrl();
    }

    private void onPlayAudioUrlSuccess() {
        urlsIterator = null;

        for (PlayBackListener listener : playBackListeners) {
            listener.onAudioPlayingStarted();
        }
    }

    private void onPlayAudioFailed() {
        UiMessages.error(this, "Broken audio!");
        binder.playNext();
    }

    private void callProgressChangedListeners(long progress, long max) {
        for (PlayBackListener listener : playBackListeners) {
            listener.onProgressChanged(progress, max);
        }
    }

    private void tryPlayNextAudioUrl() {
        if(urlsIterator == null){
            return;
        }

        if(!urlsIterator.hasNext()){
            onPlayAudioFailed();
            return;
        }

        String url = urlsIterator.next();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            onPlayAudioUrlFailed();
        }

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                onPlayAudioUrlFailed();
                return true;
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        for (PlayBackListener listener : playBackListeners) {
                            listener.onAudioPlayingComplete();
                        }
                        binder.playNext();
                    }
                });

                mediaPlayer.start();
                onPlayAudioUrlSuccess();
            }
        });

        mediaPlayer.prepareAsync();
    }

    private void onUrlsReady(Iterable<String> audios) {
        urlsIterator = audios.iterator();
        tryPlayNextAudioUrl();
    }

    private void playAudio(Audio audio) {
        isPaused = false;
        mediaPlayer.reset();

        CancelableUtils.cancelAllAndClearQueue(audioPlayingOperations);

        Cancelable urlsGettingOperation = audioUrlsProvider.getUrls(audio, new AudioUrlsProvider.OnResult() {
            @Override
            public void onResult(Iterable<String> urls) {
                onUrlsReady(urls);
            }
        });
        audioPlayingOperations.add(urlsGettingOperation);
    }

    public class PlayerBinder extends Binder implements Pauseable {
        public void setAudioUrlsProvider(AudioUrlsProvider audioUrlsProvider) {
            AudioPlayerService.this.audioUrlsProvider = audioUrlsProvider;
        }

        public void setAudios(ListWithSelectedItem<Audio> audios){
            selectedItemPositionManager = new ListSelectedItemPositionManager<Audio>(audios);
        }

        public void setAudios(SelectedItemPositionManager<Audio> selectedItemPositionManager) {
            AudioPlayerService.this.selectedItemPositionManager = selectedItemPositionManager;
        }

        public Object getPlayListTag() {
            return playListTag;
        }

        public void setPlayListTag(Object tag) {
            playListTag = tag;
        }

        public void playAudio(int position) {
            selectedItemPositionManager.setCurrentItemPosition(position);
            Audio audio = selectedItemPositionManager.getCurrentSelectedItem();
            AudioPlayerService.this.playAudio(audio);
        }

        public void playNext() {
            CancelableUtils.cancelAllAndClearQueue(audioPlayingOperations);
            if (selectedItemPositionManager.canSelectNext()) {
                Cancelable cancelable = selectedItemPositionManager.selectNextWhenAvailable(
                        new SelectedItemPositionManager.OnAvailable<Audio>() {
                    @Override
                    public void onItemAvailable(Audio audio) {
                        AudioPlayerService.this.playAudio(audio);
                    }
                });
                audioPlayingOperations.add(cancelable);
            }
        }

        public void playPrev() {
            CancelableUtils.cancelAllAndClearQueue(audioPlayingOperations);
            if (selectedItemPositionManager.canSelectPrev()) {
                Cancelable cancelable = selectedItemPositionManager.selectPrevWhenAvailable(
                        new SelectedItemPositionManager.OnAvailable<Audio>() {
                            @Override
                            public void onItemAvailable(Audio audio) {
                                AudioPlayerService.this.playAudio(audio);
                            }
                        });
                audioPlayingOperations.add(cancelable);
            }
        }

        public void seekTo(int position) {
            mediaPlayer.seekTo(position);
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public int getCurrentSeekPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        public void addPlayBackListener(PlayBackListener listener) {
            playBackListeners.add(listener);
        }

        public void removePlayBackListener(PlayBackListener listener) {
            playBackListeners.remove(listener);
        }

        public void togglePausedState() {
            if(isPaused){
                resume();
            } else {
                pause();
            }
        }

        @Override
        public void pause() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
                for (PlayBackListener listener : playBackListeners) {
                    listener.onAudioPlayingPaused();
                }
            }
        }

        @Override
        public void resume() {
            if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
                for (PlayBackListener listener : playBackListeners) {
                    listener.onAudioPlayingResumed();
                }
            }
        }

        @Override
        public boolean isPaused() {
            return isPaused;
        }

        @Override
        public boolean canPause() {
            return mediaPlayer.isPlaying();
        }

        public Audio getPlayingAudio() {
            if(!mediaPlayer.isPlaying()){
                return null;
            }

            return selectedItemPositionManager.getCurrentSelectedItem();
        }

        public List<Audio> getPlayList() {
            return selectedItemPositionManager.getItems();
        }

        public int getPlayingAudioPosition() {
            if (!mediaPlayer.isPlaying()) {
                return -1;
            }

            return selectedItemPositionManager.getCurrentItemPosition();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mediaPlayerProgressUpdater.setMediaPlayer(mediaPlayer);
        return binder;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AudioPlayerService.class);
        context.startService(intent);
    }

    public static void bind(Context context, Services.OnBind<PlayerBinder> onBind) {
        Services.bind(context, AudioPlayerService.class, onBind);
    }
}
