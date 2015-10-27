package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.strings.Strings;

/**
 * Created by stykhonenko on 26.10.15.
 */
public class TheAudioDbParser {

    private static final String DURATION_KEY_TOKEN = "\"intDuration\":\"";

    public boolean fillAudioDuration(Audio audio, String response) {
        int index = response.indexOf(DURATION_KEY_TOKEN);
        if (index > 0) {
            int duration = Strings.parseUnsignedIntToken(response, DURATION_KEY_TOKEN.length() + index);
            if (duration > 0) {
                duration /= 1000;
                audio.setDuration(duration);
                return true;
            }
        }

        return false;
    }
}
