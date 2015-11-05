package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.FilterNavigationList;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.navigation.SearchFilterNavigationList;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.KeyProvider;
import com.utilsframework.android.network.RequestManager;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class AudioFilterNavigationList extends SearchFilterNavigationList<Audio> {
    public AudioFilterNavigationList(PageNavListParams params) {
        super(params);
    }

    @Override
    protected KeyProvider<Object, Audio> getKeyProvider() {
        return AudioKeyProvider.INSTANCE;
    }

    @Override
    protected SearchQueue<Audio> getSearchQueue(InternetSearchEngine internetSearchEngine,
                                                int elementsOfPageCount, String query) {
        return internetSearchEngine.searchAudios(query, elementsOfPageCount);
    }
}
