package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class LastFmArtistTrackParser extends LastFmArtCollectionInPageParserWithAttrTag<Audio>{
    @Override
    public Audio parse(JSONObject jsonObject) throws JSONException {
        return LastFmAudioParser.parseAudio(this, jsonObject);
    }
}
