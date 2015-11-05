package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.utils.framework.strings.Strings;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class SongsFilterMultiTagNavigationList extends AudioFilterNavigationList {
    private final String[] tags;

    public SongsFilterMultiTagNavigationList(PageNavListParams params, String[] tags) {
        super(params);
        this.tags = tags;
    }

    @Override
    protected boolean satisfies(InternetSearchEngine internetSearchEngine, Audio item) throws IOException {
        List<String> topTags = internetSearchEngine.getTopTags(item);
        return Strings.containsAnyIgnoreCase(topTags, tags);
    }
}
