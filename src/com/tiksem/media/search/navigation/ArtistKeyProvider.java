package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Artist;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
class ArtistKeyProvider implements KeyProvider<Object, Artist> {
    static final ArtistKeyProvider INSTANCE = new ArtistKeyProvider();

    private ArtistKeyProvider() {

    }

    @Override
    public Object getKey(Artist value) {
        return value.getName();
    }
}
