package com.tiksem.media.search.suggestions;

import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Artist;
import com.utils.framework.suggestions.SuggestionsProvider;

import java.util.ArrayList;
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
    private String trackName;

    public ArtistSuggestionsProvider(AudioDataManager audioDataManager, int maxCount) {
        if(maxCount < 1){
            throw new IllegalArgumentException();
        }

        this.audioDataManager = audioDataManager;
        this.maxCount = maxCount;
    }

    public ArtistSuggestionsProvider(AudioDataManager audioDataManager, int maxCount, String trackName) {
        this(audioDataManager, maxCount);
        this.trackName = trackName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    @Override
    public List<Artist> getSuggestions(String query) {
        List<Artist> result = new ArrayList<Artist>();
        int artistByQueryMaxCount = maxCount;

        if (trackName != null) {
            List<Artist> artistsByTrack =
                    audioDataManager.getSuggestedArtistsByTrackName(trackName, maxCount / 3);
            result.addAll(artistsByTrack);
            artistByQueryMaxCount -= artistsByTrack.size();
        }

        List<Artist> artistsByQuery = audioDataManager.getArtists(query, artistByQueryMaxCount);
        result.addAll(artistsByQuery);
        return result;
    }
}
