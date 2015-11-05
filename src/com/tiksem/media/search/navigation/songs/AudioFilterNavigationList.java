package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.navigation.FilterNavigationList;
import com.utils.framework.KeyProvider;
import com.utilsframework.android.network.RequestManager;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class AudioFilterNavigationList extends FilterNavigationList<Audio> {
    public AudioFilterNavigationList(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    protected KeyProvider<Object, Audio> getKeyProvider() {
        return AudioKeyProvider.INSTANCE;
    }
}
