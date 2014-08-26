package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.SearchResult;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 08.03.13
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class MultiTagsArtistNavigationList extends MultiTagNavigationList<Artist> {
    public MultiTagsArtistNavigationList(Params params) {
        super(params);
    }

    @Override
    protected SearchResult<Artist> searchByTag(String tag, int pageNumber) throws Exception {
        return getInternetSearchEngine().getArtistsByTag(tag, getElementsPerPage(), pageNumber);
    }
}
