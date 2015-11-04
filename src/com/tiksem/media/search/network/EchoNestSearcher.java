package com.tiksem.media.search.network;

import com.utils.framework.io.Network;
import com.utils.framework.network.RequestExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/13/13
 * Time: 3:46 PM
 */
public class EchoNestSearcher {
    private static final String API_KEY = "ZYRJN2LAPWTND343S";
    private static final String CONSUMER_KEY = "0c2599860d3113525481859be1a1a4d1";
    private static final String SHARED_SECRET = "l6PD5zyQRS6qEiKDmrr/YQ";
    private static final String SONGS_SEARCH_ROOT_URL = "http://developer.echonest.com/api/v4/song/search";

    private RequestExecutor requestExecutor;

    public EchoNestSearcher(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    private String search(String url, Map<String,Object> params) throws IOException {
        params.put("format","json");
        params.put("api_key",API_KEY);

        return requestExecutor.executeRequest(url, params);
    }

    private String searchSongs(Map<String,Object> params) throws IOException {
        return search(SONGS_SEARCH_ROOT_URL, params);
    }

    public String searchSongs(EchoNestSongsSearchParams searchParams) throws IOException {
        Map<String,Object> params = searchParams.toMap();
        return searchSongs(params);
    }

    public String searchSongsByArtist(String artistName, EchoNestSongsSearchParams searchParams) throws IOException{
        Map<String,Object> params = searchParams.toMap();
        params.put("artist",artistName);

        return searchSongs(params);
    }

    public String searchSongsByArtist(String artistName) throws IOException{
        return searchSongsByArtist(artistName, new EchoNestSongsSearchParams());
    }

    public String searchSongsByTitle(String title, EchoNestSongsSearchParams searchParams) throws IOException{
        Map<String,Object> params = searchParams.toMap();
        params.put("artist",title);

        return searchSongs(params);
    }

    public String searchSongsByTitle(String title) throws IOException{
        return searchSongsByTitle(title, new EchoNestSongsSearchParams());
    }

    public String searchSongsByMood(String title, EchoNestSongsSearchParams searchParams) throws IOException{
        Map<String,Object> params = searchParams.toMap();
        params.put("mood",title);

        return searchSongs(params);
    }

    public String searchSongsByMood(String title) throws IOException{
        return searchSongsByTitle(title, new EchoNestSongsSearchParams());
    }

    public String getTrackInfo(String name, String artistName) throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("title", name);
        args.put("artist", artistName);
        args.put("bucket", "audio_summary");
        return search(SONGS_SEARCH_ROOT_URL, args);
    }
}
