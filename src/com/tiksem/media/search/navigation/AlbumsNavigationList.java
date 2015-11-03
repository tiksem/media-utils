package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Album;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 06.01.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class AlbumsNavigationList extends AlbumKeyNavigationList {
    public AlbumsNavigationList(InitParams initialParams) {
        super(initialParams);
    }

    @Override
    protected SearchResult<Album> search(int pageNumber) throws IOException {
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String query = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getAlbumsByName(query, elementsOfPageCount, pageNumber + 1);
    }
}
