package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class ArtistSongsNavigationList extends PageNavigationList<Audio> {
    public ArtistSongsNavigationList(InitParams<Audio> initialParams)
    {
        super(initialParams);
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws Exception{
        InternetSearchEngine internetSearchEngine = getInternetSearchEngine();
        String artistName = getQuery();
        int elementsOfPageCount = getElementsOfPageCount();
        return internetSearchEngine.getSongsOfArtist(artistName, elementsOfPageCount, pageNumber + 1);
    }
}
