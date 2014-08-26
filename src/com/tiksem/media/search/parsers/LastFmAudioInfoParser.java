package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/18/13
 * Time: 8:05 PM
 */
public class LastFmAudioInfoParser extends LastFmArtCollectionParser<Audio>{
    private Audio audio;

    public LastFmAudioInfoParser(Audio audio) {
        this.audio = audio;
    }

    @Override
    public Audio parse(JSONObject jsonObject) throws JSONException {
        JSONObject track = jsonObject.getJSONObject("track");

        String name = jsonObject.getString("name");
        Integer id = jsonObject.getInt("id");
        int duration = jsonObject.optInt("duration",0);

        audio.setName(name);
        audio.setDuration(duration);
        audio.setId(id);

        JSONObject artist = track.getJSONObject("artist");
        if(artist != null){
            String artistName = artist.getString("name");
            audio.setArtistName(artistName);
        }

        JSONObject album = track.getJSONObject("album");
        if(album != null){
            String albumName = album.getString("name");
            audio.setAlbumName(albumName);

        }

        fillAlbumArts(jsonObject, audio);

        return audio;
    }
}
