package com.tiksem.media.search.suggestions;

import android.text.TextUtils;
import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.utils.framework.suggestions.SuggestionsProvider;
import com.utils.framework.suggestions.SuggestionsProviderWithHelpWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class AudioSuggestionsProvider implements SuggestionsProvider<Audio> {
    private AudioDataManager audioDataManager;
    private int maxCount;

    public AudioSuggestionsProvider(AudioDataManager audioDataManager, int maxCount) {
        if(maxCount < 1){
            throw new IllegalArgumentException();
        }

        this.audioDataManager = audioDataManager;
        this.maxCount = maxCount;
    }

    @Override
    public List<Audio> getSuggestions(String query) {
        if (!TextUtils.isEmpty(query)) {
            return audioDataManager.getSongs(query, maxCount);
        } else {
            return new ArrayList<Audio>();
        }
    }
}
