package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.tiksem.media.data.AudioNameArtistNameLinkedSet;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.KeyProvider;
import com.utils.framework.Lists;
import com.utils.framework.algorithms.ObjectCoefficientProvider;
import com.utils.framework.algorithms.ObjectProbabilityRangeMap;
import com.utilsframework.android.network.RequestManager;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 09.03.13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class SongsYouMayLikeNavigationList extends AsyncNavigationList<Audio> {
    public static class Params {
        public int songsCountPerPage;
        public int maxCount = Integer.MAX_VALUE;
        public List<Audio> userPlaylist;
        public InternetSearchEngine internetSearchEngine;
        public RequestManager requestManager;
    }

    private AudioNameArtistNameLinkedSet songsYouMayLike;
    private AudioNameArtistNameLinkedSet userPlayListAsSet;
    private List<Audio> userPlaylist;
    private InternetSearchEngine internetSearchEngine;
    private int songsPerPageCount;
    private List<SearchQueue<Audio>> similarTracksProviders;
    private ObjectProbabilityRangeMap<Audio> songsGenerator;

    private static class AudioCoefficientProvider implements ObjectCoefficientProvider<Audio> {
        @Override
        public float get(Audio audio) {
            return audio.getTotalListeningDuration();
        }
    }

    private boolean isLastPage(){
        if (similarTracksProviders.isEmpty()) {
            return true;
        }

        return false;
    }

    private void reportEmptyOrInvalidProvider(int index) {
        userPlaylist.remove(index);
        similarTracksProviders.remove(index);
        initSongsGenerator();
    }

    private Audio generateAudio() throws IOException {
        final int index = songsGenerator.getRandomObjectIndex();
        Audio audio = userPlaylist.get(index);

        SearchQueue<Audio> similarTracksProvider = similarTracksProviders.get(index);
        if(similarTracksProvider == null){
            similarTracksProvider = internetSearchEngine.getSimilarTracks(audio, songsPerPageCount);
            similarTracksProviders.set(index, similarTracksProvider);
        }

        Audio suggestedAudio;
        while (true) {
            suggestedAudio = similarTracksProvider.get();

            if(suggestedAudio == null){
                reportEmptyOrInvalidProvider(index);
                break;
            }

            if(!userPlayListAsSet.contains(suggestedAudio) && songsYouMayLike.add(suggestedAudio)){
                break;
            }
        }

        return suggestedAudio;
    }

    public SongsYouMayLikeNavigationList(Params params) {
        super(params.requestManager, params.maxCount);

        songsYouMayLike = new AudioNameArtistNameLinkedSet();
        userPlayListAsSet = new AudioNameArtistNameLinkedSet();
        userPlayListAsSet.addAll(params.userPlaylist);
        userPlaylist = new ArrayList<Audio>(userPlayListAsSet);

        initSongsGenerator();

        similarTracksProviders = Lists.arrayListWithNulls(userPlaylist.size());

        internetSearchEngine = params.internetSearchEngine;
        songsPerPageCount = params.songsCountPerPage;
    }

    private void initSongsGenerator() {
        songsGenerator = new ObjectProbabilityRangeMap<Audio>(userPlaylist,
                new AudioCoefficientProvider());
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws IOException {
        SearchResult<Audio> searchResult = new SearchResult<Audio>();
        searchResult.isLastPage = isLastPage();
        if(searchResult.isLastPage){
            searchResult.elements = Collections.emptyList();
            return searchResult;
        }

        Audio audio = generateAudio();
        if(audio != null){
            searchResult.elements = Collections.singletonList(audio);
        } else {
            searchResult.elements = Collections.emptyList();
        }

        searchResult.isLastPage = isLastPage();

        return searchResult;
    }

    @Override
    protected void onAllDataLoaded() {
        songsYouMayLike = null;
        userPlaylist = null;
        userPlayListAsSet = null;
        similarTracksProviders = null;
    }

    @Override
    protected KeyProvider<Object, Audio> getKeyProvider() {
        return null;
    }
}
