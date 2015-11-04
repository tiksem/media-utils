package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.strings.Strings;

/**
 * Created by stykhonenko on 04.11.15.
 */
public class EchoNestParser {
    private static final String DURATION_KEY_TOKEN = "\"duration\": ";

    public boolean fillAudioDuration(Audio audio, String response) {
        int index = response.indexOf(DURATION_KEY_TOKEN);
        if (index > 0) {
            int duration = Strings.parseUnsignedIntToken(response, DURATION_KEY_TOKEN.length() + index);
            if (duration > 0) {
                audio.setDuration(duration);
                return true;
            }
        }

        return false;
    }
}
