package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Album;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class ArtistAlbumsNavigationList extends PageNavigationList<Album>{
    public ArtistAlbumsNavigationList(InitParams initialParams)
    {
        super(initialParams);
    }

    @Override
    protected SearchResult<Album> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String artistName = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getAlbumsOfArtist(artistName, elementsOfPageCount, pageNumber + 1);
    }
}
