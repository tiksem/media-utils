package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Artist;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
public abstract class ArtistKeyNavigationList extends PageNavigationList<Artist> {
    public ArtistKeyNavigationList(InitParams initialParams) {
        super(initialParams);
    }

    @Override
    protected KeyProvider<Object, Artist> getKeyProvider() {
        return null;
    }
}
