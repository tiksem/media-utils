package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/25/13
 * Time: 6:44 PM
 */
public class TagSongsNavigationList extends AudioNavigationList {
    public TagSongsNavigationList(PageNavListParams initialParams) {
        super(initialParams);
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String tag = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getSongsByTag(tag, elementsOfPageCount, pageNumber + 1);
    }
}
