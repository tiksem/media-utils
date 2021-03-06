package com.tiksem.media.search.navigation.artists;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.navigation.PageNavigationList;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
public abstract class ArtistKeyNavigationList extends PageNavigationList<Artist> {
    public ArtistKeyNavigationList(PageNavListParams initialParams) {
        super(initialParams);
    }

    @Override
    protected KeyProvider<Object, Artist> getKeyProvider() {
        return null;
    }
}
