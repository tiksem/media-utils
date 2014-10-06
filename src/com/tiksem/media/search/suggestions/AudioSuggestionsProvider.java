package com.tiksem.media.search.suggestions;

import android.text.TextUtils;
import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.data.NamedData;
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
    private String artistName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public AudioSuggestionsProvider(AudioDataManager audioDataManager, int maxCount) {
        if(maxCount < 1){
            throw new IllegalArgumentException();
        }

        this.audioDataManager = audioDataManager;
        this.maxCount = maxCount;
    }

    @Override
    public List<Audio> getSuggestions(String query) {
        List<Audio> result = new ArrayList<Audio>();
        int artistByQueryMaxCount = maxCount;

        if (artistName != null) {
            List<Audio> audiosByArtist =
                    audioDataManager.getTracksOfArtist(artistName, maxCount / 3);
            audiosByArtist = NamedData.uniqueNames(audiosByArtist);
            result.addAll(audiosByArtist);
            artistByQueryMaxCount -= audiosByArtist.size();
        }

        List<Audio> audiosByQuery = audioDataManager.getSongs(query, artistByQueryMaxCount);
        audiosByQuery = NamedData.uniqueNames(audiosByQuery);
        result.addAll(audiosByQuery);
        return result;
    }
}
