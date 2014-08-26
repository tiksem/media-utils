package com.tiksem.media.search.parsers;

import com.tiksem.media.data.ArtCollection;
import com.tiksem.media.data.ArtSize;
import com.utilsframework.android.parsers.json.JsonArrayElementParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/14/13
 * Time: 5:20 PM
 */
abstract class LastFmArtCollectionParser<T> extends JsonArrayElementParser<T> {
    private final static int[] ALBUM_ART_INDEXING_MAP = new int[]{3,3,5};

    protected String getAlbumArtUrlByIndex(JSONArray albumArts, int index) throws JSONException{
        index = ALBUM_ART_INDEXING_MAP[index];
        if(albumArts.length() == 0){
            return null;
        }
        else if(index >= albumArts.length()){
            index = albumArts.length() - 1;
        }

        JSONObject art = albumArts.getJSONObject(index);
        return art.getString("#text");
    }

    protected boolean fillAlbumArts(JSONObject jsonObject, ArtCollection album){
        try {
            JSONArray albumArts = jsonObject.getJSONArray("image");
            for (int i = 0; i < ArtSize.values().length; i++) {
                String url = getAlbumArtUrlByIndex(albumArts, i);
                if (!url.isEmpty()) {
                    album.setArtUrl(ArtSize.values()[i], url);
                } else {
                    return false;
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }
}
