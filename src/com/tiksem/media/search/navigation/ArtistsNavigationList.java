package com.tiksem.media.search.navigation;
import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/18/13
 * Time: 7:26 PM
 */
public class ArtistsNavigationList extends PageNavigationList<Artist>{
    public ArtistsNavigationList(InitParams initialParams) {
        super(initialParams);
    }

    @Override
    protected SearchResult<Artist> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String query = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.searchArtists(query, elementsOfPageCount, pageNumber + 1);
    }
}
