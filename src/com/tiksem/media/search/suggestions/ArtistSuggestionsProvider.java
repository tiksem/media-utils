package com.tiksem.media.search.suggestions;

import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Artist;
import com.utils.framework.suggestions.SuggestionsProvider;
import com.utils.framework.suggestions.SuggestionsProviderWithHelpWord;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class ArtistSuggestionsProvider implements SuggestionsProvider<Artist> {
    private AudioDataManager audioDataManager;
    private int maxCount;

    public ArtistSuggestionsProvider(AudioDataManager audioDataManager, int maxCount) {
        if(maxCount < 1){
            throw new IllegalArgumentException();
        }

        this.audioDataManager = audioDataManager;
        this.maxCount = maxCount;
    }

    @Override
    public List<Artist> getSuggestions(String query) {
        return audioDataManager.getArtists(query, maxCount);
    }
}
