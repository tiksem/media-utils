package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.KeyProvider;

import java.io.IOException;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class SearchFilterNavigationList<T> extends FilterNavigationList<T> {
    private final InternetSearchEngine internetSearchEngine;
    private final String query;
    private int elementsOfPageCount;

    public SearchFilterNavigationList(PageNavListParams params) {
        super(params.requestManager);
        internetSearchEngine = params.internetSearchEngine;
        query = params.query;
        elementsOfPageCount = params.elementsOfPageCount;
    }

    @Override
    protected final SearchQueue<T> getSearchQueue() {
        return getSearchQueue(internetSearchEngine, elementsOfPageCount, query);
    }

    protected abstract SearchQueue<T> getSearchQueue(InternetSearchEngine internetSearchEngine,
                                                          int elementsOfPageCount, String query);

    @Override
    protected final boolean satisfies(T item) throws IOException {
        return satisfies(internetSearchEngine, item);
    }

    protected abstract boolean satisfies(InternetSearchEngine internetSearchEngine, T item) throws IOException;
}
