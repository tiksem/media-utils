package com.tiksem.media.search.navigation.artists;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.strings.Strings;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class ArtistsFilterTagNavigationList extends ArtistFilterNavigationList {
    private final InternetSearchEngine internetSearchEngine;
    private final String tag;
    private final String query;
    private int elementsOfPageCount;

    public ArtistsFilterTagNavigationList(PageNavListParams params, String tag) {
        super(params.requestManager);
        this.tag = tag;
        internetSearchEngine = params.internetSearchEngine;
        query = params.query;
        elementsOfPageCount = params.elementsOfPageCount;
    }

    @Override
    protected SearchQueue<Artist> getSearchQueue() {
        return internetSearchEngine.searchArtists(query, elementsOfPageCount);
    }

    @Override
    protected boolean satisfies(Artist item) throws IOException {
        List<String> topTags = internetSearchEngine.getTopTags(item);
        return Strings.containsIgnoreCase(topTags, tag);
    }
}
