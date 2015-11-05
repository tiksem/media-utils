package com.tiksem.media.search.navigation.artists;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.tiksem.media.search.navigation.PageNavListParams;

import java.io.IOException;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/25/13
 * Time: 7:04 PM
 */
public class TagArtistsNavigationList extends ArtistKeyNavigationList {
    public TagArtistsNavigationList(PageNavListParams initialParams) {
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
