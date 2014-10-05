package com.tiksem.media.search.suggestions;

import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Album;
import com.tiksem.media.data.Artist;
import com.utils.framework.suggestions.SuggestionsProvider;
import com.utils.framework.suggestions.SuggestionsProviderWithHelpWord;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class AlbumSuggestionsProvider implements SuggestionsProvider<Album> {
    private AudioDataManager audioDataManager;
    private int maxCount;

    public AlbumSuggestionsProvider(AudioDataManager audioDataManager, int maxCount) {
        if(maxCount < 1){
            throw new IllegalArgumentException();
        }

        this.audioDataManager = audioDataManager;
        this.maxCount = maxCount;
    }

    @Override
    public List<Album> getSuggestions(String query) {
        return audioDataManager.getAlbums(query, maxCount);
    }
}
