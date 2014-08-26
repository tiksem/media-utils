package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 09.03.13
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
class LastFmSimilarTrackParser extends LastFmArtCollectionParser<Audio>{
    @Override
    public Audio parse(JSONObject jsonObject) throws JSONException {
        return LastFmAudioParser.parseAudio(this, jsonObject);
    }
}
