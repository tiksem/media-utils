package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Artist;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/25/13
 * Time: 6:55 PM
 */
public class LastFmGenreArtistParser extends LastFmArtCollectionInPageParser<Artist>{
    @Override
    public Artist parse(JSONObject jsonObject) throws JSONException {
        return LastFmArtistParser.parseArtist(this, jsonObject);
    }

    @Override
    public int getElementsCount(JSONObject responseObject, JSONArray objects) throws JSONException {
        if(objects.length() <= 0){
            setLastPage(true);
        }
        
        return -1;
    }
}
