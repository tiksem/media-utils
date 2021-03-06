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
    private String tag;

    public ArtistsFilterTagNavigationList(PageNavListParams params, String tag) {
        super(params);
        this.tag = tag;
    }

    @Override
    protected boolean satisfies(InternetSearchEngine internetSearchEngine, Artist item) throws IOException {
        List<String> topTags = internetSearchEngine.getTopTags(item);
        return Strings.containsIgnoreCase(topTags, tag);
    }
}
