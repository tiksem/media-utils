package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
class AudioKeyProvider implements KeyProvider<Object, Audio> {
    static final AudioKeyProvider INSTANCE = new AudioKeyProvider();

    private static final String DIVIDER = "_%_";

    protected AudioKeyProvider() {
    }

    @Override
    public Object getKey(Audio value) {
        return value.getName() + DIVIDER + value.getArtistName();
    }
}
