package com.tiksem.media.search.navigation;

import com.tiksem.media.data.Audio;
import com.tiksem.media.data.AudioNameArtistNameUniqueSet;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.algorithms.ObjectCoefficientProvider;
import com.utils.framework.algorithms.ObjectProbabilityRangeMap;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 09.03.13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class SongsYouMayLikeNavigationList extends AsyncNavigationList<Audio> {
    private static final int SONGS_YOU_MAY_LIKE_SAFE_COUNT = 5000;

    public static class Params{
        public int songsCount;
        public int songsCountPerPage;
        public List<Audio> userPlaylist;
        public InternetSearchEngine internetSearchEngine;
    }

    private AudioNameArtistNameUniqueSet songsYouMayLike;
    private AudioNameArtistNameUniqueSet userPlayListAsSet;
    private List<Audio> userPlaylist;
    private InternetSearchEngine internetSearchEngine;
    private int songsPerPageCount;
    private Queue<Audio>[] similarTracksProviders;
    private int requestCount = 0;
    private ObjectProbabilityRangeMap<Audio> songsGenerator;

    private static class AudioCoefficientProvider implements ObjectCoefficientProvider<Audio> {
        @Override
        public float get(Audio audio) {
            return audio.getTotalListeningDuration();
        }
    }

    private boolean isLastPage(){
        boolean enoughElements = songsYouMayLike.size() >= getMaxElementsCount();
        boolean safeCountViolation = requestCount > SONGS_YOU_MAY_LIKE_SAFE_COUNT;
        return enoughElements || safeCountViolation;
    }

    private Audio generateAudio(){
        int index = songsGenerator.getRandomObjectIndex();
        Audio audio = userPlaylist.get(index);

        Queue<Audio> similarTracksProvider = similarTracksProviders[index];
        if(similarTracksProvider == null){
            similarTracksProvider = internetSearchEngine.getSimilarTracks(audio, songsPerPageCount);
            similarTracksProviders[index] = similarTracksProvider;
        }

        Audio suggestedAudio = null;
        while (true) {
            suggestedAudio = similarTracksProvider.poll();

            if(suggestedAudio == null){
                break;
            }

            if(!userPlayListAsSet.contains(suggestedAudio) && songsYouMayLike.add(suggestedAudio)){
                break;
            }
        }

        requestCount++;
        return suggestedAudio;
    }

    public SongsYouMayLikeNavigationList(Params params) {
        super(Collections.<Audio>emptyList(), params.songsCount);

        songsYouMayLike = new AudioNameArtistNameUniqueSet();
        userPlayListAsSet = new AudioNameArtistNameUniqueSet(new LinkedHashSet());
        userPlayListAsSet.addAll(params.userPlaylist);
        userPlaylist = new ArrayList<Audio>(userPlayListAsSet);

        songsGenerator = new ObjectProbabilityRangeMap<Audio>(userPlaylist,
                new AudioCoefficientProvider());

        similarTracksProviders = new Queue[userPlaylist.size()];

        internetSearchEngine = params.internetSearchEngine;
        songsPerPageCount = params.songsCountPerPage;
    }

    @Override
    protected SearchResult<Audio> search(int pageNumber) throws Exception {
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
}
