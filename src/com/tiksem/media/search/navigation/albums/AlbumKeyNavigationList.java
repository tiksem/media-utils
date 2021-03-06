package com.tiksem.media.search.navigation.albums;

import com.tiksem.media.data.Album;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.tiksem.media.search.navigation.PageNavigationList;
import com.utils.framework.KeyProvider;

/**
 * Created by stykhonenko on 03.11.15.
 */
public abstract class AlbumKeyNavigationList extends PageNavigationList<Album> {
    public AlbumKeyNavigationList(PageNavListParams initialParams) {
        super(initialParams);
    }

    @Override
    protected KeyProvider<Object, Album> getKeyProvider() {
        return AlbumKeyProvider.INSTANCE;
    }
}
