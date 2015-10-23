package com.tiksem.media.search.parsers;

import com.tiksem.media.data.ArtCollection;
import com.utils.framework.parsers.json.ExtendedJSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/14/13
 * Time: 8:13 PM
 */
abstract class LastFmArtCollectionInPageParser<T> extends LastFmArtCollectionParser<T>{
    private boolean isLastPage = true;

    public boolean isLastPage() {
        return isLastPage;
    }

    protected final void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    @Override
    protected boolean fillAlbumArts(JSONObject jsonObject, ArtCollection album) {
        setLastPage(false);
        return super.fillAlbumArts(jsonObject, album);
    }
}
