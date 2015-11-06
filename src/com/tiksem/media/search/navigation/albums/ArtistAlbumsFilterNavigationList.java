package com.tiksem.media.search.navigation.albums;

import com.tiksem.media.data.Album;
import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.PageNavListParams;
import com.utils.framework.strings.Strings;

import java.io.IOException;

/**
 * Created by stykhonenko on 06.11.15.
 */
public class ArtistAlbumsFilterNavigationList extends AlbumFilterNavigationList {
    private final String artistName;

    public ArtistAlbumsFilterNavigationList(PageNavListParams params, String artistName) {
        super(params);
        this.artistName = artistName;

        if (!Strings.containsAnyIgnoreCase(params.query, " " + artistName, artistName + " ")) {
            params.query += " " + artistName;
        }
    }

    @Override
    protected boolean satisfies(InternetSearchEngine internetSearchEngine, Album item) throws IOException {
        return item.getArtistName().equals(artistName);
    }
}
