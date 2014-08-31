package com.tiksem.media.search;

import com.tiksem.media.data.Album;
import com.tiksem.media.data.ArtCollection;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.search.correction.CorrectionUtilities;
import com.tiksem.media.search.network.*;
import com.tiksem.media.search.parsers.LastFmCorrectedAudioInfoParser;
import com.tiksem.media.search.parsers.LastFmResultParser;
import com.tiksem.media.search.parsers.VkResultParser;
import com.utils.framework.collections.cache.*;
import com.utils.framework.collections.queue.PageLazyQueue;
import com.utils.framework.io.TextLoader;
import com.utils.framework.io.TextLoaderConfig;
import com.utilsframework.android.ErrorListener;
import com.utilsframework.android.string.Strings;
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
    private Cache memoryCache;
    private Cache discCache;
    private CacheCombination cache;
    private TextLoader textLoader;

    private void combineCaches(){
        cache = new CacheCombination(memoryCache, discCache);
    }

    public InternetSearchEngine(Cache memoryCache, Cache discCache, TextLoader textLoader) {
        this.memoryCache = memoryCache;
        this.discCache = discCache;

        if(discCache == null){
            this.discCache = new EmptyCache();
        }

        if(memoryCache == null){
            this.memoryCache = new EmptyCache();
        }

        if(textLoader == null){
            textLoader = new TextLoader(new TextLoaderConfig());
        }
        lastFMSearcher = new LastFMSearcher(textLoader);
        vkSearcher = new VkSearcher(textLoader);

        combineCaches();
    }

    public InternetSearchEngine(int memoryCacheSize, int discCacheSize,
                                CacheDirectoryPathGenerator discCacheDir,
                                TextLoader textLoader
                                ) {
        if(memoryCacheSize > 0){
            memoryCache = new ObjectLruCache(memoryCacheSize);
        } else {
            memoryCache = new EmptyCache();
        }

        if(discCacheSize > 0){
            discCache = new ObjectLruDiskCache(discCacheSize, discCacheDir);
        } else {
            discCache = new EmptyCache();
        }

        if(textLoader == null){
            textLoader = new TextLoader(new TextLoaderConfig());
        }
        lastFMSearcher = new LastFMSearcher(textLoader);
        vkSearcher = new VkSearcher(textLoader);

        combineCaches();
    }

    public InternetSearchEngine(int memoryCacheSize){
        this(memoryCacheSize, -1, null, null);
    }

    public InternetSearchEngine(){
        this(-1);
    }

    private interface LastFmSearchResultProvider<T>{
        String search(String query, LastFMSearchParams searchParams) throws IOException;
        SearchResult<T> parse(String response) throws JSONException;
    }

    private interface ResultProvider<T>{
        String search(Object query) throws IOException;
        T parse(String response) throws JSONException;
    }


    private static class LastFmSearchResultParams<T>{
        LastFmSearchResultProvider<T> resultProvider;
        String methodName;
        String query;
        int maxCount;
        int page;
    }

    private static class ResultParams<T>{
        ResultProvider<T> resultProvider;
        String methodName;
        Object query;
    }

    private <T> SearchResult<T> getLastFmSearchResult(LastFmSearchResultParams<T> lastFmParams) throws IOException{
        LastFMSearchParams lastFMSearchParams = new LastFMSearchParams();
        lastFMSearchParams.limit = lastFmParams.maxCount;
        lastFMSearchParams.page = lastFmParams.page;

        try{
            String key = Strings.joinObjects("_", lastFmParams.methodName, lastFmParams.query,
                    lastFmParams.maxCount, lastFmParams.page).toString();
            SearchResult<T> searchResult = CacheUtils.saveGet(cache, key);

            if(searchResult == null){
                String response = lastFmParams.resultProvider.search(lastFmParams.query, lastFMSearchParams);
                searchResult = lastFmParams.resultProvider.parse(response);
                cache.put(key, searchResult);
            }

            return searchResult;
        }
        catch (JSONException e){
            throw new InvalidResponseException(e.getMessage());
        }
    }

    private <T> T getResult(ResultParams<T> resultParams) {
        try{
            String key = Strings.joinObjects("_", resultParams.methodName, resultParams.query).toString();
            T searchResult = CacheUtils.saveGet(cache, key);

            if(searchResult == null){
                String response = resultParams.resultProvider.search(resultParams.query);
                searchResult = resultParams.resultProvider.parse(response);
                cache.put(key, searchResult);
            }

            return searchResult;
        }
        catch (Exception e){
            return null;
        }
    }

    private <T> List<T> getResultList(ResultParams<List<T>> resultParams) {
        List<T> list = getResult(resultParams);
        if(list == null){
            return new ArrayList<T>();
        }

        return list;
    }

    public SearchResult<Album> getAlbumsByName(String query, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Album> lastFmParams = new LastFmSearchResultParams<Album>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "getAlbumsByName";
        lastFmParams.query = query;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Album>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.getAlbumsByName(query, searchParams);
            }

            @Override
            public SearchResult<Album> parse(String response) throws JSONException {
                return lastFmResultParser.parseAlbums(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public SearchResult<Album> getAlbumsOfArtist(String artistName, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Album> lastFmParams = new LastFmSearchResultParams<Album>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "getAlbumsOfArtist";
        lastFmParams.query = artistName;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Album>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.getAlbumsOfArtist(query, searchParams);
            }

            @Override
            public SearchResult<Album> parse(String response) throws JSONException {
                return lastFmResultParser.getAlbumsOfArtist(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
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

    public List<Audio> getSongsOfAlbum(final Album album) {
        long id = album.getId();

        try {
            if(id < 0){
                updateAlbumId(album);
                id = album.getId();
                if(id < 0){
                    return Collections.emptyList();
                }
            }

            ResultParams<List<Audio>> resultParams = new ResultParams<List<Audio>>();
            resultParams.query = id;
            resultParams.methodName = "getSongsOfAlbum";
            resultParams.resultProvider = new ResultProvider<List<Audio>>() {
                @Override
                public String search(Object id) throws IOException {
                    return lastFMSearcher.getTracksByAlbumId((Integer)id);
                }

                @Override
                public List<Audio> parse(String response) throws JSONException {
                    return lastFmResultParser.getSongsOfAlbum(response, album);
                }
            };

            return getResultList(resultParams);

        } catch (JSONException e) {
            return Collections.emptyList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public SearchResult<Audio> searchAudios(String query, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Audio> lastFmParams = new LastFmSearchResultParams<Audio>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "searchAudios";
        lastFmParams.query = query;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Audio>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.searchTracks(query, searchParams);
            }

            @Override
            public SearchResult<Audio> parse(String response) throws JSONException {
                return lastFmResultParser.parseTracks(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public SearchResult<Audio> getSongsOfArtist(String artistName, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Audio> lastFmParams = new LastFmSearchResultParams<Audio>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "getSongsOfArtist";
        lastFmParams.query = artistName;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Audio>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.getTracksOfArtist(query, searchParams);
            }

            @Override
            public SearchResult<Audio> parse(String response) throws JSONException {
                return lastFmResultParser.getSongsOfArtist(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public SearchResult<Audio> getSongsByTag(String tag, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Audio> lastFmParams = new LastFmSearchResultParams<Audio>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "getSongsByTag";
        lastFmParams.query = tag;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Audio>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.getTracksByTag(query, searchParams);
            }

            @Override
            public SearchResult<Audio> parse(String response) throws JSONException {
                return lastFmResultParser.getSongsByTag(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public SearchResult<Artist> getArtistsByTag(String tag, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Artist> lastFmParams = new LastFmSearchResultParams<Artist>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "getArtistsByTag";
        lastFmParams.query = tag;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Artist>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.getArtistsByTag(query, searchParams);
            }

            @Override
            public SearchResult<Artist> parse(String response) throws JSONException {
                return lastFmResultParser.getArtistsByTag(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public boolean fillAudioInfo(Audio audio){
        String name = audio.getName();
        String artistName = audio.getArtistName();

        if(name == null || artistName == null){
            return false;
        }

        try {
            String response = lastFMSearcher.getTrackInfoByNameAndArtistName(name, artistName);
            return lastFmResultParser.fillAudioInfo(response, audio);
        } catch (IOException e) {
            return false;
        }
    }

    public Iterable<String> getAudioUrls(final Audio audio) {
        String name = audio.getName();
        String artistName = audio.getArtistName();

        if (name == null || artistName == null) {
            return Collections.emptyList();
        }

        String query = artistName + " " + name;
        fillAudioInfo(audio);

        ResultParams<List<String>> resultParams = new ResultParams<List<String>>();
        resultParams.methodName = "getAudioUrls";
        resultParams.query = query;
        resultParams.resultProvider = new ResultProvider<List<String>>() {
            @Override
            public String search(Object query) throws IOException {
                VkAudioSearchParams searchParams = new VkAudioSearchParams();
                searchParams.setCount(AUDIO_URLS_COUNT);
                return vkSearcher.searchAudios(query.toString(), searchParams);
            }

            @Override
            public List<String> parse(String response) throws JSONException {
                return vkResultParser.getAudioUrls(response, audio);
            }
        };

        return getResultList(resultParams);
    }

    public SearchResult<Artist> searchArtists(String query, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<Artist> lastFmParams = new LastFmSearchResultParams<Artist>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "searchArtists";
        lastFmParams.query = query;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Artist>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.searchArtists(query, searchParams);
            }

            @Override
            public SearchResult<Artist> parse(String response) throws JSONException {
                return lastFmResultParser.parseArtists(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
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

    public Queue<Audio> getSimilarTracks(final Audio audio, final int countPerPage, final ErrorListener errorListener) {
        return new PageLazyQueue<Audio>() {
            boolean isLastPage = false;

            @Override
            protected List<Audio> loadData(int pageNumber) {
                if(isLastPage){
                    return null;
                }

                try {
                    SearchResult<Audio> result = getSimilarTracks(audio, countPerPage, pageNumber);
                    isLastPage = result.isLastPage;
                    return result.elements;

                } catch (IOException e) {
                    if(errorListener != null){
                        errorListener.onError(e);
                    }
                    return null;
                }
            }
        };
    }

    public Queue<Audio> getSimilarTracks(final Audio audio, final int countPerPage) {
        return getSimilarTracks(audio, countPerPage, null);
    }

    public SearchResult<Artist> getSimilarArtists(Artist artist, int maxCount, int page) throws IOException {
        String artistName = artist.getName();
        if(artistName == null){
            throw new NullPointerException("artistName == null");
        }

        LastFmSearchResultParams<Artist> lastFmParams = new LastFmSearchResultParams<Artist>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "getSimilarArtists";
        lastFmParams.query = artistName;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<Artist>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.getSimilarArtists(query, searchParams);
            }

            @Override
            public SearchResult<Artist> parse(String response) throws JSONException {
                return lastFmResultParser.getSimilarArtists(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public SearchResult<String> searchTags(String query, int maxCount, int page) throws IOException {
        LastFmSearchResultParams<String> lastFmParams = new LastFmSearchResultParams<String>();
        lastFmParams.maxCount = maxCount;
        lastFmParams.page = page;
        lastFmParams.methodName = "searchTags";
        lastFmParams.query = query;

        lastFmParams.resultProvider = new LastFmSearchResultProvider<String>() {
            @Override
            public String search(String query, LastFMSearchParams searchParams) throws IOException {
                return lastFMSearcher.searchTags(query, searchParams);
            }

            @Override
            public SearchResult<String> parse(String response) throws JSONException {
                return lastFmResultParser.searchTags(response);
            }
        };

        return getLastFmSearchResult(lastFmParams);
    }

    public SearchResult<String> searchTagsHandleErrors(String query, int maxCount, int page){
        try {
            return searchTags(query, maxCount, page);
        } catch (IOException e) {
            return SearchResult.empty();
        }
    }

    public LastFmCorrectedAudioInfoParser.Info getCorrectedTrackInfo(String artistName, String name)
            throws IOException {
        String query = name;
        if(!CorrectionUtilities.matchUnknownPattern(artistName)){
            query += " " + artistName;
        }

        String response = lastFMSearcher.searchTracks(query);
        LastFmCorrectedAudioInfoParser.Info correctedTrackInfo =
                lastFmResultParser.getCorrectedTrackInfo(response);
        return correctedTrackInfo;
    }

    public CorrectedTrackInfo getCorrectedTrackInfo(Audio audio) throws IOException {
        String name = audio.getName();
        String artistName = audio.getArtistName();

        LastFmCorrectedAudioInfoParser.Info correctedTrackInfo = getCorrectedTrackInfo(artistName, name);
        if(correctedTrackInfo == null){
            return null;
        }

        String replacedName = CorrectionUtilities.replaceArtistNameInName(correctedTrackInfo.artistName,
                correctedTrackInfo.name);

        if(replacedName != null){
            name = replacedName;
            artistName = correctedTrackInfo.artistName;
            correctedTrackInfo = getCorrectedTrackInfo(artistName, name);
        }

        CorrectedTrackInfo result = new CorrectedTrackInfo();
        result.name = correctedTrackInfo.name;
        result.artistName = correctedTrackInfo.artistName;
        return result;
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

    public boolean tryFillAlbumName(Audio audio) {
        try {
            Album album = getAlbumByArtistNameAndTrackName(audio.getArtistName(), audio.getName());
            if(album == null || !album.getArtistName().equals(audio.getArtistName())){
                return false;
            }

            audio.setAlbumName(album.getName());
            audio.setAlbumId(-1);
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
            Album temp = getAlbumByNameAndArtistName(album.getName(), album.getArtistName());
            if (temp != null) {
                album.cloneArtUrlsFrom(temp);
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean tryFillAlbumArts(Artist artist) {
        try {
            Artist temp = getArtistByName(artist.getName());
            if (temp != null) {
                artist.cloneArtUrlsFrom(temp);
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
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
}
