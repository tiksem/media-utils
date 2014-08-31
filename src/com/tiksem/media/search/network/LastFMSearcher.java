package com.tiksem.media.search.network;

import com.utils.framework.Reflection;
import com.utils.framework.io.TextLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 26.12.12
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public class LastFMSearcher {
    private static final String ACCESS_TOKEN = "460d4db802c6da0ef0d45c7cb32fc687";
    private static final String ACCESS_TOKEN_KEY = "api_key";
    private static final String ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
    private static final String PLAYLIST_FETCH_URL =
            "http://lastfm-api-ext.appspot.com/2.0/?" +
                    "method=playlist.fetch" +
                    "&outtype=json" +
                    "&playlistURL=lastfm://playlist/album/";

    private TextLoader textLoader;

    protected final String search(Map<String, Object> params) throws IOException {
        params.put(ACCESS_TOKEN_KEY, ACCESS_TOKEN);
        params.put("format","json");
        return textLoader.getTextFromUrl(ROOT_URL, params);
    }

    protected final String searchUsingExtensionAPI(Map<String, Object> params) throws IOException{
        params.put(ACCESS_TOKEN_KEY, ACCESS_TOKEN);
        params.put("format","json");
        return textLoader.getTextFromUrl(ROOT_URL, params);
    }

    protected final <T> String search(T params) throws IOException {
        return search(Reflection.objectToPropertyMap(params));
    }

    public String getAlbumsByName(String albumName, LastFMSearchParams additionalParams) throws IOException{
        Map<String, Object> params = Reflection.objectToPropertyMap(additionalParams);
        params.put("album",albumName);
        params.put("method","album.search");
        return search(params);
    }

    public String getAlbumsByName(String albumName) throws IOException{
        return getAlbumsByName(albumName, new LastFMSearchParams());
    }

    public String getAlbumByNameAndArtistName(String name, String artistName) throws IOException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("artist",artistName);
        params.put("album",name);
        params.put("method","album.getinfo");
        return search(params);
    }

    public String getTracksByAlbumId(int id) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append(PLAYLIST_FETCH_URL);
        url.append(id);
        url.append('&');
        url.append(ACCESS_TOKEN_KEY);
        url.append('=');
        url.append(ACCESS_TOKEN);
        return textLoader.getTextFromUrl(url.toString());
    }

    public String searchTracks(String query, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("track",query);
        params.put("method","track.search");
        return search(params);
    }

    public String searchTracks(String query) throws IOException {
        return searchTracks(query, new LastFMSearchParams());
    }

    public String getTracksOfArtist(String artistName, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","artist.gettoptracks");
        params.put("artist",artistName);
        return search(params);
    }

    public String getAlbumsOfArtist(String artistName, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","artist.gettopalbums");
        params.put("artist",artistName);
        return search(params);
    }

    public String searchArtists(String query, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","artist.search");
        params.put("artist",query);
        return search(params);
    }

    public String getArtistInfo(String artistName) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("artist",artistName);
        params.put("method","artist.getinfo");
        return search(params);
    }

    public String getTracksByTag(String genre, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","tag.gettoptracks");
        params.put("tag", genre);
        return search(params);
    }

    public String getArtistsByTag(String genre, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","tag.gettopartists");
        params.put("tag", genre);
        return search(params);
    }

    public String getTrackInfoByNameAndArtistName(String name, String artistName) throws IOException{
        LastFMSearchParams lastFMSearchParams = new LastFMSearchParams();
        lastFMSearchParams.autocorrect = 1;
        Map<String, Object> params = Reflection.objectToPropertyMap(lastFMSearchParams);
        params.put("artist",artistName);
        params.put("track",name);
        params.put("method","track.getInfo");
        return search(params);
    }

    public String getAlbumInfoByNameAndArtistName(String name, String artistName) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("artist",artistName);
        params.put("album",name);
        params.put("method","album.getinfo");
        return search(params);
    }

    public String getSimilarTracks(String name, String artistName, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","track.getsimilar");
        params.put("track", name);
        params.put("artist", artistName);
        return search(params);
    }

    public String getSimilarArtists(String artistName, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","artist.getsimilar");
        params.put("artist", artistName);
        return search(params);
    }

    public String getSimilarTags(String tagName, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","tag.getsimilar");
        params.put("tag", tagName);
        return search(params);
    }

    public String searchTags(String query, LastFMSearchParams searchParams) throws IOException {
        Map<String, Object> params = Reflection.objectToPropertyMap(searchParams);
        params.put("method","tag.search");
        params.put("tag", query);
        return search(params);
    }

    public LastFMSearcher(TextLoader textLoader) {
        this.textLoader = textLoader;
    }
}
