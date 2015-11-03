package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Album;
import com.tiksem.media.data.Audio;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
public class AlbumKeyProvider implements KeyProvider<Object, Album> {
    static final AlbumKeyProvider INSTANCE = new AlbumKeyProvider();

    private static final String DIVIDER = "_%_";

    protected AlbumKeyProvider() {
    }

    @Override
    public Object getKey(Album value) {
        return value.getName() + DIVIDER + value.getArtistName();
    }
}
