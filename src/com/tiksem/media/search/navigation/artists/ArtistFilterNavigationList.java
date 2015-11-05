package com.tiksem.media.search.navigation.artists;

import com.tiksem.media.data.Artist;
import com.tiksem.media.search.navigation.FilterNavigationList;
import com.utils.framework.KeyProvider;
import com.utilsframework.android.network.RequestManager;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class ArtistFilterNavigationList extends FilterNavigationList<Artist> {
    public ArtistFilterNavigationList(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    protected KeyProvider<Object, Artist> getKeyProvider() {
        return ArtistKeyProvider.INSTANCE;
    }
}
