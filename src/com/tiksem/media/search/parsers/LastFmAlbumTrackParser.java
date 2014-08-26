package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Album;
import com.tiksem.media.data.Audio;
import com.utilsframework.android.parsers.json.JsonArrayElementParser;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/14/13
 * Time: 8:21 PM
 */
class LastFmAlbumTrackParser extends JsonArrayElementParser<Audio> {
    private Album album;

    public LastFmAlbumTrackParser(Album album) {
        this.album = album;
    }

    @Override
    public Audio parse(JSONObject jsonObject) throws JSONException {
        Audio audio = Audio.createInternetAudio();

        String name = jsonObject.getString("title");
        Integer duration = jsonObject.optInt("duration", 0);
        String artistName = album.getArtistName();

        audio.setDuration(duration);
        audio.setName(name);
        audio.setArtistName(artistName);

        audio.cloneArtUrlsFrom(album);

        return audio;
    }
}
