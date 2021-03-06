package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.navigation.MultiTagNavigationList;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
public abstract class MultiTagAudioNavigationList extends MultiTagNavigationList<Audio> {
    public MultiTagAudioNavigationList(Params params) {
        super(params);
    }

    @Override
    protected KeyProvider<Object, Audio> getKeyProvider() {
        return AudioKeyProvider.INSTANCE;
    }
}
