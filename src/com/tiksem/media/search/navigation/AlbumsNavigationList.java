package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Album;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 06.01.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class AlbumsNavigationList extends PageNavigationList<Album>{
    public AlbumsNavigationList(InitParams<Album> initialParams) {
        super(initialParams);
    }

    @Override
    protected SearchResult<Album> search(int pageNumber) throws Exception{
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String query = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getAlbumsByName(query, elementsOfPageCount, pageNumber + 1);
    }
}
