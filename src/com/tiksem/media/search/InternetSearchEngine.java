package com.tiksem.media.search;

import com.tiksem.media.data.*;
import com.tiksem.media.playback.UrlsProvider;
import com.tiksem.media.search.network.*;
import com.tiksem.media.search.parsers.*;
import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.CollectionUtils;
import com.utils.framework.Transformer;
import com.utils.framework.collections.NavigationList;
import com.utils.framework.network.RequestExecutor;
import com.utilsframework.android.IOErrorListener;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 02.01.13
 * Time: 2:52
 * To change this template use File | Settings | File Templates.
 */
public class InternetSearchEngine {
    private static final int AUDIO_URLS_COUNT = 200;

    private LastFMSearcher lastFMSearcher;
    private LastFmResultParser lastFmResultParser = new LastFmResultParser();
    private VkResultParser vkResultParser = new VkResultParser();
    private VkSearcher vkSearcher;
    private TheAudioDbSearcher audioDbSearcher;
    private TheAudioDbParser audioDbParser = new TheAudioDbParser();
    private EchoNestSearcher echoNestSearcher;
    private EchoNestParser echoNestParser = new EchoNestParser();

    public InternetSearchEngine(RequestExecutor requestExecutor) {
        lastFMSearcher = new LastFMSearcher(requestExecutor);
        vkSearcher = new VkSearcher(requestExecutor);
        audioDbSearcher = new TheAudioDbSearcher(requestExecutor);
        echoNestSearcher = new EchoNestSearcher(requestExecutor);
    }

    private interface LastFmSearchResultProvider<T>{
        String search(String query, LastFMSearchParams searchParams) throws IOException;
        SearchResult<T> parse(String response) throws JSONException;
    }

    private interface ResultProvider<T>{
        String search() throws IOException;
        T parse(String response) throws JSONException;
    }

    public SearchResult<Album> searchAlbums(String query, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;

        String response = lastFMSearcher.getAlbumsByName(query, searchParams);
        try {
            return lastFmResultParser.parseAlbums(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchQueue<Album> searchAlbums(final String query, final int itemsPerPage) {
        return new SearchQueue<Album>() {
            @Override
            protected SearchResult<Album> loadData(int pageNumber) throws IOException {
                return searchAlbums(query, itemsPerPage, pageNumber);
            }
        };
    }

    public SearchResult<Album> getAlbumsOfArtist(String artistName, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;
        String response = lastFMSearcher.getAlbumsOfArtist(artistName, searchParams);
        try {
            return lastFmResultParser.getAlbumsOfArtist(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    private void updateAlbumId(Album album) throws IOException, JSONException {
        String name = album.getName();
        String artistName = album.getArtistName();

        if(name == null || artistName == null){
            throw new NullPointerException();
        }

        String response = lastFMSearcher.getAlbumByNameAndArtistName(name, artistName);
        int id = lastFmResultParser.getAlbumId(response);
        album.setId(id);
    }

    public List<Audio> getSongsOfAlbum(final Album album) throws IOException {
        try {
            String response = lastFMSearcher.getAlbumByNameAndArtistName(album.getName(), album.getArtistName());
            return lastFmResultParser.getSongsOfAlbum(response, album);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchResult<Audio> searchAudios(String query, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;

        String response = lastFMSearcher.searchTracks(query, searchParams);
        try {
            return lastFmResultParser.parseTracks(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchQueue<Audio> searchAudios(final String query, final int itemsPerPage) {
        return new SearchQueue<Audio>() {
            @Override
            protected SearchResult<Audio> loadData(int pageNumber) throws IOException {
                return searchAudios(query, itemsPerPage, pageNumber);
            }
        };
    }

    public SearchResult<Audio> getSongsOfArtist(String artistName, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;

        String response = lastFMSearcher.getTracksOfArtist(artistName, searchParams);
        try {
            return lastFmResultParser.getSongsOfArtist(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchResult<Audio> getSongsByTag(String tag, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;

        String response = lastFMSearcher.getTracksByTag(tag, searchParams);
        try {
            return lastFmResultParser.getSongsByTag(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchResult<Artist> getArtistsByTag(String tag, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;

        String response = lastFMSearcher.getArtistsByTag(tag, searchParams);
        try {
            return lastFmResultParser.getArtistsByTag(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public boolean fillAudioDuration(Audio audio){
        try {
            if (fillAudioDurationUsingTheEchoNest(audio)) {
                return true;
            }

            if (fillAudioDurationUsingTheDb(audio)) {
                return true;
            }

            return fillAudioDurationUsingLastFm(audio);
        } catch (IOException e) {
            return false;
        }
    }

    public ArtCollection getArts(Audio audio) throws IOException {
        String response = lastFMSearcher.getTrackInfoByNameAndArtistName(audio.getName(), audio.getArtistName());
        try {
            return lastFmResultParser.getArtsOfAudio(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    private boolean fillAudioDurationUsingTheDb(Audio audio) {
        try {
            String response = audioDbSearcher.searchTrack(audio.getName(), audio.getArtistName());
            return audioDbParser.fillAudioDuration(audio, response);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean fillAudioDurationUsingTheEchoNest(Audio audio) {
        try {
            String response = echoNestSearcher.getTrackInfo(audio.getName(), audio.getArtistName());
            return echoNestParser.fillAudioDuration(audio, response);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean fillAudioDurationUsingLastFm(Audio audio) throws IOException {
        String response;
        String mbid = audio.getMbid();
        if (mbid == null) {
            String name = audio.getName();
            String artistName = audio.getArtistName();

            if (name == null || artistName == null) {
                return false;
            }

            response = lastFMSearcher.getTrackInfoByNameAndArtistName(name, artistName);
        } else {
            response = lastFMSearcher.getTrackInfoByMbid(mbid);
        }

        return lastFmResultParser.fillAudioDuration(response, audio);
    }

    private abstract class AudioUrlsProvider implements UrlsProvider {
        Audio audio;

        public AudioUrlsProvider(Audio audio) {
            if (audio == null) {
                throw new NullPointerException();
            }

            this.audio = audio;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AudioUrlsProvider)) {
                return false;
            }

            Audio another = ((AudioUrlsProvider)o).audio;

            long id = audio.getId();
            if (id > 0) {
                return another.getId() == id;
            } else {
                if (another.getId() > 0) {
                    return false;
                } else {
                    String url = audio.getUrl();
                    if (url != null && url.equals(another.getUrl())) {
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public int hashCode() {
            long id = audio.getId();
            return (int) (id ^ (id >>> 32));
        }
    }

    public class VkUrlsProvider extends AudioUrlsProvider {
        private List<UrlQueryData> queryDataList;

        public VkUrlsProvider(Audio audio) {
            super(audio);
        }

        public List<UrlQueryData> getQueryDataList() {
            return queryDataList;
        }

        @Override
        public List<String> getUrls() throws IOException {
            if (queryDataList == null) {
                queryDataList = getAudioUrls(audio);
            }
            return CollectionUtils.transformNonCopy(queryDataList, new Transformer<UrlQueryData, String>() {
                @Override
                public String get(UrlQueryData data) {
                    return data.getUrl();
                }
            });
        }
    }

    public class LocalUrlProvider extends AudioUrlsProvider {
        public LocalUrlProvider(Audio audio) {
            super(audio);
        }

        @Override
        public List<String> getUrls() throws IOException {
            return Collections.singletonList(audio.getUrl());
        }
    }

    public List<UrlsProvider> getUrlsProviders(final List<Audio> audios) {
        return new AbstractList<UrlsProvider>() {
            private Map<Integer, UrlsProvider> cached = new HashMap<>();

            @Override
            public UrlsProvider get(int location) {
                UrlsProvider provider = cached.get(location);
                if (provider == null) {
                    Audio audio = audios.get(location);
                    String url = audio.getUrl();
                    if (url == null) {
                        provider = new VkUrlsProvider(audio);
                    } else {
                        provider = new LocalUrlProvider(audio);
                    }

                    cached.put(location, provider);
                }

                return provider;
            }

            @Override
            public int size() {
                if (audios instanceof NavigationList) {
                    return ((NavigationList) audios).getElementsCount();
                }

                return audios.size();
            }
        };
    }

    public List<UrlQueryData> getAudioUrls(final Audio audio) throws IOException {
        String name = audio.getName();
        String artistName = audio.getArtistName();

        if (name == null || artistName == null) {
            return Collections.emptyList();
        }

        String query = artistName + " " + name;
        fillAudioDuration(audio);

        List<UrlQueryData> result = getAudioUrls(audio, query);
        if (result.isEmpty()) {
            result = getAudioUrls(audio, name + " " + artistName);
            if (result.isEmpty()) {
                result = getAudioUrls(audio, name);
            }
        }

        return result;
    }

    private List<UrlQueryData> getAudioUrls(Audio audio, String query) throws IOException {
        VkAudioSearchParams searchParams = new VkAudioSearchParams();
        searchParams.setCount(AUDIO_URLS_COUNT);
        String response = vkSearcher.searchAudios(query, searchParams);
        try {
            return vkResultParser.getAudioUrls(response, audio);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchResult<Artist> searchArtists(String query, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.page = page;
        searchParams.limit = maxCount;

        String response = lastFMSearcher.searchArtists(query, searchParams);
        try {
            return lastFmResultParser.parseArtists(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchQueue<Artist> searchArtists(final String query, final int itemsPerPage) {
        return new SearchQueue<Artist>() {
            @Override
            protected SearchResult<Artist> loadData(int pageNumber) throws IOException {
                return searchArtists(query, itemsPerPage, pageNumber);
            }
        };
    }

    public SearchResult<Audio> getSimilarTracks(Audio audio, int maxCount, int page) throws IOException {
        final String name = audio.getName();
        final String artistName = audio.getArtistName();
        if(artistName == null || name == null){
            throw new NullPointerException("artistName == null || name == null");
        }

        LastFMSearchParams lastFMSearchParams = new LastFMSearchParams();
        lastFMSearchParams.autocorrect = 1;
        lastFMSearchParams.limit = maxCount;
        lastFMSearchParams.page = page;

        String response = lastFMSearcher.getSimilarTracks(name, artistName, lastFMSearchParams);
        try {
            return lastFmResultParser.getSimilarTracks(response);
        } catch (JSONException e) {
            throw new InvalidResponseException(e.getMessage());
        }
    }

    public SearchQueue<Audio> getSimilarTracks(final Audio audio, final int countPerPage) {
        return new SearchQueue<Audio>() {
            @Override
            protected SearchResult<Audio> loadData(int pageNumber) throws IOException {
                return getSimilarTracks(audio, countPerPage, pageNumber);
            }
        };
    }

    public SearchResult<Artist> getSimilarArtists(Artist artist, int maxCount, int page) throws IOException {
        String artistName = artist.getName();
        if(artistName == null){
            throw new NullPointerException("artistName == null");
        }

        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.limit = maxCount;
        searchParams.page = page;

        String response = lastFMSearcher.getSimilarArtists(artistName, searchParams);
        try {
            return lastFmResultParser.getSimilarArtists(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchResult<String> searchTags(String query, int maxCount, int page) throws IOException {
        LastFMSearchParams searchParams = new LastFMSearchParams();
        searchParams.limit = maxCount;
        searchParams.page = page;

        String response = lastFMSearcher.searchTags(query, searchParams);
        try {
            return lastFmResultParser.searchTags(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public SearchResult<String> searchTagsHandleErrors(String query, int maxCount, int page){
        try {
            return searchTags(query, maxCount, page);
        } catch (IOException e) {
            return SearchResult.empty();
        }
    }

    public Album getAlbumByNameAndArtistName(String albumName, String artistName) throws IOException {
        String json = lastFMSearcher.getAlbumInfoByNameAndArtistName(albumName, artistName);
        try {
            return lastFmResultParser.parseAlbum(json);
        } catch (JSONException e) {
            throw new InvalidResponseException(e);
        }
    }

    public Album getAlbumByArtistNameAndTrackName(String artistName, String trackName) throws IOException {
        String json = lastFMSearcher.getTrackInfoByNameAndArtistName(trackName, artistName);
        try {
            return lastFmResultParser.parseAlbumFromTrackInfo(json);
        } catch (JSONException e) {
            throw new InvalidResponseException(e);
        }
    }

    public boolean tryFillAlbum(Audio audio) {
        try {
            Album album = getAlbumByArtistNameAndTrackName(audio.getArtistName(), audio.getName());
            if(album == null || !album.getArtistName().equals(audio.getArtistName())){
                return false;
            }

            audio.setAlbumName(album.getName());
            audio.setAlbumId(-1);
            audio.cloneArtUrlsFrom(album);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Artist getArtistByName(String artistName) throws IOException {
        String json = lastFMSearcher.getArtistInfo(artistName);
        try {
            return lastFmResultParser.parseArtist(json);
        } catch (JSONException e) {
            throw new InvalidResponseException(e);
        }
    }

    public boolean tryFillAlbumArts(Album album) {
        try {
            ArtCollection temp = getAlbumByNameAndArtistName(album.getName(), album.getArtistName());
            if (temp != null) {
                album.cloneArtUrlsFrom(temp);
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public ArtCollection getArts(Artist artist) throws IOException {
        return getArtistByName(artist.getName());
    }

    public ArtCollection getArts(Album album) throws IOException {
        return getAlbumByNameAndArtistName(album.getName(), album.getArtistName());
    }

    public boolean tryFillAlbumArts(ArtCollection artCollection) {
        if(artCollection instanceof Album){
            return tryFillAlbumArts((Album) artCollection);
        } else if(artCollection instanceof Artist) {
            return tryFillAlbumArts((Artist) artCollection);
        }

        throw new IllegalArgumentException("Unsuuported ArtCollection type " +
                artCollection.getClass().getCanonicalName());
    }

    public List<String> getSuggestedArtistNamesByTrackName(String trackName, int maxCount) {
        try {
            SearchResult<Audio> searchResult = searchAudios(trackName, maxCount, 0);
            ArrayList<String> result = new ArrayList<String>();
            for(Audio audio : searchResult.elements){
                String artistName = audio.getArtistName();
                if (artistName != null) {
                    result.add(artistName);
                }
            }
            return result;

        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    public List<Artist> getArtistsByNames(List<String> artistNames) {
        List<Artist> result = new ArrayList<Artist>(artistNames.size());
        for(String artistName : artistNames){
            try {
                Artist artist = getArtistByName(artistName);
                result.add(artist);
            } catch (IOException e) {

            }
        }

        CollectionUtils.unique(result, NamedData.<Artist>equalsIgnoreCase());

        return result;
    }

    public List<Artist> getSuggestedArtistsByTrackName(String trackName, int maxCount) {
        return getArtistsByNames(getSuggestedArtistNamesByTrackName(trackName, maxCount));
    }

    public List<String> getTopTags(Artist artist) throws IOException {
        String response = lastFMSearcher.getArtistTopTags(artist.getName());
        try {
            return lastFmResultParser.parseTopTags(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }

    public List<String> getTopTags(Audio audio) throws IOException {
        String response = lastFMSearcher.getSongTopTags(audio.getName(), audio.getArtistName());
        try {
            return lastFmResultParser.parseTopTags(response);
        } catch (JSONException e) {
            throw new RequestJsonException(e);
        }
    }
}
