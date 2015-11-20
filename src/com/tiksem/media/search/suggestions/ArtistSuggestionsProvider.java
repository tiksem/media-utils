package com.tiksem.media.search.suggestions;

import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.NamedData;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.CollectionUtils;
import com.utils.framework.Transformer;
import com.utils.framework.suggestions.SuggestionsProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class ArtistSuggestionsProvider implements SuggestionsProvider<String> {
    private InternetSearchEngine internetSearchEngine;
    private int maxCount;
    private String trackName;

    public ArtistSuggestionsProvider(InternetSearchEngine internetSearchEngine, int maxCount) {
        if(maxCount < 1){
            throw new IllegalArgumentException();
        }

        this.internetSearchEngine = internetSearchEngine;
        this.maxCount = maxCount;
    }

    public ArtistSuggestionsProvider(InternetSearchEngine internetSearchEngine, int maxCount, String trackName) {
        this(internetSearchEngine, maxCount);
        this.trackName = trackName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    @Override
    public List<String> getSuggestions(String query) {
        List<String> result = new ArrayList<>();

        if (trackName != null) {
            result.addAll(internetSearchEngine.getSuggestedArtistNamesByTrackName(
                    trackName, Math.max(maxCount / 3, 1)));
        }

        result = CollectionUtils.unique(result);
        try {
            List<Artist> artists = internetSearchEngine.
                    searchArtists(query, maxCount - result.size(), 1).elements;
            CollectionUtils.transformAndAdd(artists, result, new Transformer<Artist, String>() {
                @Override
                public String get(Artist artist) {
                    return artist.getName();
                }
            });
        } catch (IOException e) {
            return result;
        }

        return CollectionUtils.unique(result);
    }
}
