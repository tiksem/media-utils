package com.tiksem.media.playback;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.tiksem.media.data.Audio;
import com.utils.framework.Cancelable;
import com.utils.framework.collections.ListSelectedItemPositionManager;
import com.utils.framework.collections.ListWithSelectedItem;
import com.utils.framework.collections.SelectedItemPositionManager;
import com.utilsframework.android.view.UiMessages;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * User: Tikhonenko.S
 * Date: 17.07.14
 * Time: 22:19
 */
public class AudioPlayerService extends Service {
    private PlayerBinder binder = new PlayerBinder();
    private SelectedItemPositionManager<Audio> selectedItemPositionManager;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private AudioUrlsProvider audioUrlsProvider = new LocalAudioUrlsProvider();
    private Iterator<String> urlsIterator;

    private Cancelable urlsGettingOperation;

    private void onPlayAudioUrlFailed() {
        tryPlayNextAudioUrl();
    }

    private void onPlayAudioUrlSuccess() {
        urlsIterator = null;
    }

    private void onPlayAudioFailed() {
        UiMessages.error(this, "Broken audio!");
        binder.playNext();
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
                mediaPlayer.start();
                onPlayAudioUrlSuccess();
            }
        });
    }

    private void onUrlsReady(Iterable<String> audios) {
        urlsIterator = audios.iterator();
    }

    private void playAudio(Audio audio) {
        mediaPlayer.reset();

        if (urlsGettingOperation != null) {
            urlsGettingOperation.cancel();
        }

        urlsGettingOperation = audioUrlsProvider.getUrls(audio, new AudioUrlsProvider.OnResult() {
            @Override
            public void onResult(Iterable<String> urls) {
                onUrlsReady(urls);
            }
        });
    }

    public class PlayerBinder extends Binder {
        public void setAudioUrlsProvider(AudioUrlsProvider audioUrlsProvider) {
            AudioPlayerService.this.audioUrlsProvider = audioUrlsProvider;
        }

        public void setAudios(ListWithSelectedItem<Audio> audios){
            selectedItemPositionManager = new ListSelectedItemPositionManager<Audio>(audios);
        }

        public void setAudios(SelectedItemPositionManager<Audio> selectedItemPositionManager) {
            AudioPlayerService.this.selectedItemPositionManager = selectedItemPositionManager;
        }

        public void playAudio(int position) {
            selectedItemPositionManager.setCurrentItemPosition(position);
            Audio audio = selectedItemPositionManager.getCurrentSelectedItem();
            AudioPlayerService.this.playAudio(audio);
        }

        public void playNext() {
            Audio audio = selectedItemPositionManager.selectNext();
            AudioPlayerService.this.playAudio(audio);
        }

        public void playPrev() {
            Audio audio = selectedItemPositionManager.selectPrev();
            AudioPlayerService.this.playAudio(audio);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
