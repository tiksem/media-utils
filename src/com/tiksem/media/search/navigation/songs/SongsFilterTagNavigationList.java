package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.queue.SearchQueue;
import com.utilsframework.android.network.RequestManager;

import java.io.IOException;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class SongsFilterTagNavigationList extends AudioFilterNavigationList {
    private final String tag;
    private final InternetSearchEngine internetSearchEngine;
    private final String query;
    private final int elementsOfPageCount;

    public SongsFilterTagNavigationList(PageNavListParams params, String tag) {
        super(params.requestManager);
        this.tag = tag;
        internetSearchEngine = params.internetSearchEngine;
        query = params.query;
        elementsOfPageCount = params.elementsOfPageCount;
    }

    @Override
    protected boolean satisfies(Audio item) throws IOException {
        return internetSearchEngine.getTopTags(item).contains(tag);
    }

    @Override
    protected SearchQueue<Audio> getSearchQueue() {
        return internetSearchEngine.searchAudios(query, elementsOfPageCount);
    }
}
