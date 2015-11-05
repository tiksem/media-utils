package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class ArtistSongsNavigationList extends AudioNavigationList {
    public ArtistSongsNavigationList(PageNavListParams initialParams)
    {
        super(initialParams);
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String artistName = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getSongsOfArtist(artistName, elementsOfPageCount, pageNumber + 1);
    }
}
