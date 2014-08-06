package com.tiksem.media.playback;

import com.tiksem.media.data.Audio;
import com.utils.framework.Cancelable;

import java.util.Collections;

/**
 * User: Tikhonenko.S
 * Date: 17.07.14
 * Time: 22:38
 */
public class LocalAudioUrlsProvider implements AudioUrlsProvider {
    private boolean canceled = false;

    @Override
    public Cancelable getUrls(Audio audio, OnResult onResult) {
        if(audio.isLocal()){
            onResult.onResult(Collections.singletonList(audio.getUrl()));
        } else {
            getRemoteAudioUrls(audio, onResult);
        }

        return new Cancelable() {
            @Override
            public void cancel() {
                canceled = true;
            }
        };
    }

    public boolean isCanceled() {
        return canceled;
    }

    protected void getRemoteAudioUrls(Audio audio, OnResult onResult) {
        throw new UnsupportedOperationException();
    }
}
