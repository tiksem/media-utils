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
        List<Audio> audiosByArtist = null;

        if (artistName != null) {
            audiosByArtist =
                    audioDataManager.getSongs(artistName + " " + query, maxCount);
            audiosByArtist = NamedData.uniqueNames(audiosByArtist);
        }

        List<Audio> audiosByQuery = audioDataManager.getSongs(query, maxCount);
        audiosByQuery = NamedData.uniqueNames(audiosByQuery);

        if(audiosByArtist != null){
            int audiosByArtistCount = maxCount / 3;
            int audiosByQueryCount = maxCount - audiosByArtistCount;

            if(audiosByQuery.size() < audiosByQueryCount){
                audiosByArtistCount = maxCount - audiosByQuery.size();
            }

            if(audiosByArtist.size() < audiosByArtistCount){
                audiosByQueryCount = maxCount - audiosByArtist.size();
            }

            audiosByArtistCount = Math.min(audiosByArtistCount, audiosByArtist.size());
            audiosByQueryCount = Math.min(audiosByQueryCount, audiosByQuery.size());

            if(audiosByQuery.size() != audiosByQueryCount){
                audiosByQuery = audiosByQuery.subList(0, audiosByQueryCount);
            }

            if(audiosByArtist.size() != audiosByArtistCount){
                audiosByArtist = audiosByArtist.subList(0, audiosByArtistCount);
            }

            ArrayList<Audio> result = new ArrayList<Audio>();
            result.addAll(audiosByArtist);
            result.addAll(audiosByQuery);
            return result;

        } else {
            return audiosByQuery;
        }
    }
}
