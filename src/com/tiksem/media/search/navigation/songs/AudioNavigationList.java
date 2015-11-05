package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.navigation.PageNavigationList;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
public abstract class AudioNavigationList extends PageNavigationList<Audio> {
    public AudioNavigationList(PageNavListParams initialParams) {
        super(initialParams);
    }

    @Override
    protected KeyProvider<Object, Audio> getKeyProvider() {
        return AudioKeyProvider.INSTANCE;
    }
}
