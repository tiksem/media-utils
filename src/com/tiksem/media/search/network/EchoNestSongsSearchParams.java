package com.tiksem.media.search.network;

import com.utils.framework.Reflection;
import com.utilsframework.android.string.Strings;

import java.util.Map;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/13/13
 * Time: 3:52 PM
 */
public class EchoNestSongsSearchParams {
    public static enum Sort{
        tempo_asc, 
        duration_asc, 
        loudness_asc, 
        artist_familiarity_asc, 
        artist_hotttnesss_asc, 
        artist_start_year_asc, 
        artist_start_year_desc, 
        artist_end_year_asc, 
        artist_end_year_desc, 
        song_hotttness_asc, 
        latitude_asc, 
        longitude_asc, 
        mode_asc, 
        key_asc, 
        tempo_desc, 
        duration_desc, 
        loudness_desc, 
        artist_familiarity_desc, 
        artist_hotttnesss_desc, 
        song_hotttnesss_desc, 
        latitude_desc, 
        longitude_desc, 
        mode_desc, 
        key_desc, 
        energy_asc, 
        energy_desc, 
        danceability_asc, 
        danceability_desc
    }

    public static enum Bucket{
        audio_summary,
        artist_familiarity,
        artist_hotttnesss,
        artist_location,
        song_hotttnesss,
        song_type,
        tracks
    }

    public Map<String,Object> toMap(){
        return Reflection.objectToPropertyMap(this, new Reflection.ParamTransformer() {
            @Override
            public Object transform(String paramName, Object value) {

                if (value.getClass() == Sort.class) {
                    String valueAsString = value.toString();
                    int index = valueAsString.lastIndexOf('_');
                    return Strings.setCharAt(valueAsString, index, '-');
                }

                return value;
            }
        });
    }
}
