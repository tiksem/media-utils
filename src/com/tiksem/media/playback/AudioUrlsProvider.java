package com.tiksem.media.playback;

import com.tiksem.media.data.Audio;
import com.utils.framework.Cancelable;

/**
 * User: Tikhonenko.S
 * Date: 17.07.14
 * Time: 22:37
 */
public interface AudioUrlsProvider {
    interface OnResult {
        void onResult(Iterable<String> urls);
    }

    Cancelable getUrls(Audio audio, OnResult onResult);
}
