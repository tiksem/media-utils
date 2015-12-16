package com.tiksem.media.search.parsers;

import com.tiksem.media.Config;
import com.tiksem.media.data.Audio;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/14/13
 * Time: 5:18 PM
 */
class LastFmAudioParser extends LastFmArtCollectionInPageParser<Audio>{
    public static Audio parseAudio(LastFmArtCollectionParser<Audio> artCollectionParser, JSONObject jsonObject)
            throws JSONException
    {
        Audio audio = Audio.createInternetAudio();

        String name = jsonObject.optString("name", jsonObject.optString("title"));
        Integer duration = jsonObject.optInt("duration", 0);

        JSONObject artist = jsonObject.optJSONObject("artist");
        String artistName;
        if(artist == null){
            artistName = jsonObject.optString("artist");
        } else {
            artistName = artist.optString("name");
        }

        audio.setDuration(duration);
        audio.setName(name);
        audio.setArtistName(artistName);

        if (Config.SONGS_YOU_MAY_LIKE) {
            audio.setLastFMUrl(jsonObject.optString("url"));
        }

        String mbid = jsonObject.optString("mbid");
        audio.setMbid(mbid);

        artCollectionParser.fillAlbumArts(jsonObject, audio);

        return audio;
    }

    @Override
    public Audio parse(JSONObject jsonObject) throws JSONException {
        return parseAudio(this, jsonObject);
    }
}
