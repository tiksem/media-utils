package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Artist;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/18/13
 * Time: 7:13 PM
 */
public class LastFmArtistParser extends LastFmArtCollectionInPageParser<Artist>{
    public static Artist parseArtist(LastFmArtCollectionParser artCollectionParser,
                                   JSONObject jsonObject) throws JSONException
    {
        Artist artist = Artist.createInternetArtist();

        String artistName = jsonObject.getString("name");
        artist.setName(artistName);
        if(!artCollectionParser.fillAlbumArts(jsonObject, artist)){
            return null;
        }

        return artist;
    }

    @Override
    public Artist parse(JSONObject jsonObject) throws JSONException {
        return parseArtist(this, jsonObject);
    }
}
