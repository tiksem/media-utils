package com.tiksem.media.search.navigation.albums;

import com.tiksem.media.data.Album;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.tiksem.media.search.navigation.albums.AlbumKeyNavigationList;
import com.tiksem.media.search.navigation.PageNavListParams;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class ArtistAlbumsNavigationList extends AlbumKeyNavigationList {
    public ArtistAlbumsNavigationList(PageNavListParams initialParams)
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
