package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.SearchResult;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 08.03.13
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public class MultiTagsSongsNavigationList extends MultiTagAudioNavigationList {
    public MultiTagsSongsNavigationList(Params params) {
        super(params);
    }

    @Override
    protected SearchResult<Audio> searchByTag(String tag, int pageNumber) throws IOException {
        return getInternetSearchEngine().getSongsByTag(tag, getElementsPerPage(), pageNumber);
    }
}
