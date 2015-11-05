package com.tiksem.media.search.navigation;

import android.widget.Filter;
import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.ArrayUtils;
import com.utils.framework.strings.Strings;
import com.utilsframework.android.network.RequestManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class ArtistsFilterTagNavigationList extends FilterNavigationList<Artist> {
    private final InternetSearchEngine internetSearchEngine;
    private final String tag;

    public ArtistsFilterTagNavigationList(PageNavListParams params, String tag) {
        super(params.requestManager);
        this.tag = tag;
        internetSearchEngine = params.internetSearchEngine;
        SearchQueue<Artist> artists =
                internetSearchEngine.searchArtists(params.query, params.elementsOfPageCount);
        setSearchQueue(artists);
    }

    @Override
    protected boolean satisfies(Artist item) throws IOException {
        List<String> topTags = internetSearchEngine.getTopTags(item);
        return Strings.containsIgnoreCase(topTags, tag);
    }
}
