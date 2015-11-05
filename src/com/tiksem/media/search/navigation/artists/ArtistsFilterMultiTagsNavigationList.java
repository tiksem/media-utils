package com.tiksem.media.search.navigation.artists;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.strings.Strings;
import com.utilsframework.android.network.RequestManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class ArtistsFilterMultiTagsNavigationList extends ArtistFilterNavigationList {
    private final String[] tags;

    public ArtistsFilterMultiTagsNavigationList(PageNavListParams params, String[] tags) {
        super(params);
        this.tags = tags;
    }

    @Override
    protected boolean satisfies(InternetSearchEngine internetSearchEngine, Artist item) throws IOException {
        List<String> topTags = internetSearchEngine.getTopTags(item);
        return Strings.containsAnyIgnoreCase(topTags, tags);
    }
}
