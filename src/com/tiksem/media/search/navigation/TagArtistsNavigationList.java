package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/25/13
 * Time: 7:04 PM
 */
public class TagArtistsNavigationList extends PageNavigationList<Artist>{
    public TagArtistsNavigationList(InitParams initialParams) {
        super(initialParams);
    }

    @Override
    protected SearchResult<Artist> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String genre = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getArtistsByTag(genre, elementsOfPageCount, pageNumber + 1);
    }
}
