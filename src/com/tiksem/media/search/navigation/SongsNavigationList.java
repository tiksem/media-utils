package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 10.02.13
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class SongsNavigationList extends PageNavigationList<Audio>{
    public SongsNavigationList(InitParams initialParams) {
        super(initialParams);
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String query = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.searchAudios(query, elementsOfPageCount, pageNumber + 1);
    }
}
