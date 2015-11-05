package com.tiksem.media.search.navigation.artists;

import com.tiksem.media.data.Artist;
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
public abstract class ArtistFilterNavigationList extends SearchFilterNavigationList<Artist> {
    public ArtistFilterNavigationList(PageNavListParams params) {
        super(params);
    }

    @Override
    protected KeyProvider<Object, Artist> getKeyProvider() {
        return ArtistKeyProvider.INSTANCE;
    }

    @Override
    protected SearchQueue<Artist> getSearchQueue(InternetSearchEngine internetSearchEngine,
                                                 int elementsOfPageCount, String query) {
        return internetSearchEngine.searchArtists(query, elementsOfPageCount);
    }
}
