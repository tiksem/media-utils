package com.tiksem.media.search.parsers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 3/11/13
 * Time: 3:41 PM
 */
public class LastFmTagParser extends LastFmArtCollectionInPageParser<String>{
    @Override
    public String parse(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("name");
    }
}
