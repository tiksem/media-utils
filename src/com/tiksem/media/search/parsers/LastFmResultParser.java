package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Album;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.Primitive;
import com.utils.framework.parsers.json.ExtendedJSONObject;
import com.utils.framework.parsers.json.JsonArrayElementParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 26.12.12
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class LastFmResultParser {
    private <T> SearchResult<T> parsePageElements(String response,
                                                  LastFmArtCollectionInPageParser parser,
                                                  String... path)
            throws JSONException
    {
        ExtendedJSONObject jsonObject = new ExtendedJSONObject(response);
        SearchResult<T> result = new SearchResult<T>();
        result.elements = jsonObject.parseJsonArrayFromPath(parser, path);
        result.isLastPage = parser.isLastPage();

        return result;
    }

    public SearchResult<Album> parseAlbums(String response) throws JSONException {
        LastFmAlbumParser parser = new LastFmAlbumParser();
        return parsePageElements(response, parser, "results", "albummatches", "album");
    }

    public SearchResult<Album> getAlbumsOfArtist(String response) throws JSONException {
        LastFmArtistAlbumParser parser = new LastFmArtistAlbumParser();
        return parsePageElements(response, parser, "topalbums", "album");
    }

    public List<Audio> getSongsOfAlbum(String response, Album album) throws JSONException {
        ExtendedJSONObject jsonObject = new ExtendedJSONObject(response);
        JsonArrayElementParser<Audio> parser = new LastFmAlbumTrackParser(album);

        return jsonObject.parseJsonArrayFromPath(parser, "trackList");
    }

    public int getAlbumId(String response) throws JSONException {
        JSONObject root = new JSONObject(response);
        JSONObject album = root.getJSONObject("album");
        return album.getInt("id");
    }

    public SearchResult<Audio> getSongsOfArtist(String response) throws JSONException {
        LastFmArtistTrackParser parser = new LastFmArtistTrackParser();
        return parsePageElements(response, parser, "toptracks", "track");
    }

    public SearchResult<Audio> getSongsByTag(String response) throws JSONException {
        return getSongsOfArtist(response);
    }

    public SearchResult<Audio> parseTracks(String response) throws JSONException {
        LastFmAudioParser parser = new LastFmAudioParser();
        return parsePageElements(response, parser, "results", "trackmatches", "track");
    }

    public SearchResult<Artist> parseArtists(String response) throws JSONException {
        LastFmArtistParser parser = new LastFmArtistParser();
        return parsePageElements(response, parser, "results", "artistmatches", "artist");
    }

    public SearchResult<Artist> getArtistsByTag(String response) throws JSONException {
        LastFmGenreArtistParser parser = new LastFmGenreArtistParser();
        return parsePageElements(response, parser, "topartists", "artist");
    }

    public LastFmCorrectedAudioInfoParser.Info getCorrectedTrackInfo(String response){
        try {
            LastFmCorrectedAudioInfoParser parser = new LastFmCorrectedAudioInfoParser();
            ExtendedJSONObject jsonObject = new ExtendedJSONObject(response);

            List<LastFmCorrectedAudioInfoParser.Info> tracks =
                jsonObject.parseJsonArrayFromPath(parser, "results", "trackmatches", "track");

            return Collections.max(tracks, new Comparator<LastFmCorrectedAudioInfoParser.Info>() {
                @Override
                public int compare(LastFmCorrectedAudioInfoParser.Info a,
                                   LastFmCorrectedAudioInfoParser.Info b) {
                    return Primitive.compare(a.listenersCount, b.listenersCount);
                }
            });
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean fillAudioInfo(String response, Audio audio){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject track = jsonObject.getJSONObject("track");
            int duration = track.optInt("duration", 0);
            audio.setDuration(duration);

        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    public SearchResult<Audio> getSimilarTracks(String response) throws JSONException {
        SearchResult<Audio> searchResult = new SearchResult<Audio>();
        LastFmSimilarTrackParser parser = new LastFmSimilarTrackParser();
        ExtendedJSONObject jsonObject = new ExtendedJSONObject(response);
        searchResult.elements = jsonObject.parseJsonArrayFromPath(parser, "similartracks", "track");
        searchResult.isLastPage = true;
        return searchResult;
    }

    public SearchResult<Artist> getSimilarArtists(String response) throws JSONException {
        LastFmArtistParser parser = new LastFmArtistParser();
        return parsePageElements(response, parser, "similarartists", "artist");
    }

    public SearchResult<String> searchTags(String response) throws JSONException {
        LastFmTagParser parser = new LastFmTagParser();
        return parsePageElements(response, parser, "results", "tagmatches", "tag");
    }

    public Album parseAlbum(String response) throws JSONException {
        LastFmAlbumParser albumParser = new LastFmAlbumParser();
        JSONObject jsonObject = new JSONObject(response);
        return albumParser.parse(jsonObject.getJSONObject("album"));
    }

    public Album parseAlbumFromTrackInfo(String response) throws JSONException {
        LastFmAlbumParser albumParser = new LastFmAlbumParser();
        JSONObject jsonObject = new JSONObject(response);
        return albumParser.parse(jsonObject.getJSONObject("track").getJSONObject("album"));
    }

    public Artist parseArtist(String response) throws JSONException {
        LastFmArtistParser artistParser = new LastFmArtistParser();
        JSONObject jsonObject = new JSONObject(response);
        return artistParser.parse(jsonObject.getJSONObject("artist"));
    }
}
