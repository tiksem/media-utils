package com.tiksem.media.search.navigation.artists;
import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.tiksem.media.search.navigation.PageNavListParams;

import java.io.IOException;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/18/13
 * Time: 7:26 PM
 */
public class ArtistsNavigationList extends ArtistKeyNavigationList {
    public ArtistsNavigationList(PageNavListParams initialParams) {
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
