package com.tiksem.media.search.navigation.songs;

import com.tiksem.media.data.Audio;
import com.tiksem.media.data.Genres;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.tiksem.media.search.navigation.AsyncNavigationList;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.KeyProvider;
import com.utils.framework.Lists;
import com.utilsframework.android.network.RequestManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by stykhonenko on 01.12.15.
 */
public class TopSongsByGenresNavigationList extends AsyncNavigationList<Audio> {
    private final int countPerPage;
    private final List<String> genres;
    private InternetSearchEngine internetSearchEngine;
    private List<SearchQueue<Audio>> audioByGenresProviders;
    private Random random;

    public TopSongsByGenresNavigationList(RequestManager requestManager,
                                          InternetSearchEngine internetSearchEngine,
                                          int countPerPage,
                                          int maxCount) {
        super(requestManager, maxCount);
        this.internetSearchEngine = internetSearchEngine;
        this.countPerPage = countPerPage;
        genres = Genres.getGenresList();
        audioByGenresProviders = Lists.arrayListWithNulls(genres.size());
        random = new Random();
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws IOException {
        Audio audio = generateNextAudio();
        if (audio == null) {
            return SearchResult.empty();
        } else {
            return SearchResult.single(audio);
        }
    }

    private Audio generateNextAudio() throws IOException {
        while (!genres.isEmpty()) {
            int index = random.nextInt(genres.size());
            String genre = genres.get(index);
            SearchQueue<Audio> provider = audioByGenresProviders.get(index);
            if (provider == null) {
                provider = internetSearchEngine.getSongsByTagSearchQueue(genre, countPerPage);
                audioByGenresProviders.set(index, provider);
            }

            Audio audio = provider.get();
            if (audio == null) {
                genres.remove(index);
                audioByGenresProviders.remove(index);
            } else {
                return audio;
            }
        }

        return null;
    }

    @Override
    protected KeyProvider<Object, Audio> getKeyProvider() {
        return AudioKeyProvider.INSTANCE;
    }
}
