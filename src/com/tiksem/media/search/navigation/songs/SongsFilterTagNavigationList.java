package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
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
public class SongsFilterTagNavigationList extends AudioFilterNavigationList {
    private final String tag;

    public SongsFilterTagNavigationList(PageNavListParams params, String tag) {
        super(params);
        this.tag = tag;
    }

    @Override
    protected boolean satisfies(InternetSearchEngine internetSearchEngine, Audio item) throws IOException {
        List<String> topTags = internetSearchEngine.getTopTags(item);
        return Strings.containsIgnoreCase(topTags, tag);
    }
}
