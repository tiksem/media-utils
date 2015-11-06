package com.tiksem.media.search.navigation.albums;

import com.tiksem.media.data.Album;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.navigation.SearchFilterNavigationList;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class AlbumFilterNavigationList extends SearchFilterNavigationList<Album> {
    public AlbumFilterNavigationList(PageNavListParams params) {
        super(params);
    }

    @Override
    protected KeyProvider<Object, Album> getKeyProvider() {
        return AlbumKeyProvider.INSTANCE;
    }

    @Override
    protected SearchQueue<Album> getSearchQueue(InternetSearchEngine internetSearchEngine,
                                                 int elementsOfPageCount, String query) {
        return internetSearchEngine.searchAlbums(query, elementsOfPageCount);
    }
}
