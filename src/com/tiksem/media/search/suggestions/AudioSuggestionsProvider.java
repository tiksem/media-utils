package com.tiksem.media.search.suggestions;

import android.text.TextUtils;
import com.tiksem.media.AudioDataManager;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.data.NamedData;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.collections.iterator.ReverseIterator;
import com.utils.framework.strings.Strings;
import com.utils.framework.suggestions.SuggestionsProvider;
import com.utils.framework.suggestions.SuggestionsProviderWithHelpWord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class AudioSuggestionsProvider implements SuggestionsProvider<String> {
    private InternetSearchEngine internetSearchEngine;
    private int maxCount;
    private int minCount;
    private String artistName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /* minCount is used to define minimum suggestions count, while removing audios with wrong artist,
     * see getSuggestions for details */
    public AudioSuggestionsProvider(InternetSearchEngine internetSearchEngine, int minCount, int maxCount) {
        if (minCount > maxCount) {
            throw new IllegalArgumentException("minCount > maxCount");
        }

        this.internetSearchEngine = internetSearchEngine;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public List<String> getSuggestions(String query) {
        try {
            if (artistName == null) {
                return NamedData.namedDataListToNameList(
                        internetSearchEngine.searchAudios(query, maxCount, 1).elements);
            } else {
                List<String> result = new ArrayList<>();
                List<Audio> audios = internetSearchEngine.searchAudios(query, maxCount * 2, 1).elements;
                audios = NamedData.uniqueNames(audios);

                int removedCount = 0;
                for (int i = audios.size() - 1; i >= 0; i--) {
                    Audio audio = audios.get(i);
                    if (result.size() - removedCount > minCount &&
                            !Strings.equalsIgnoreCase(audio.getArtistName(), artistName)) {
                        removedCount++;
                    } else {
                        result.add(audio.getName());
                    }
                }

                Collections.reverse(result);
                return result;
            }
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
