package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Album;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
class LastFmArtistAlbumParser extends LastFmArtCollectionInPageParserWithAttrTag<Album>{
    @Override
    public Album parse(JSONObject jsonObject) throws JSONException {
        Album album = Album.createInternetAlbum();

        String name = jsonObject.getString("name");
        album.setName(name);

        JSONObject artist = jsonObject.getJSONObject("artist");
        String artistName = artist.getString("name");
        album.setArtistName(artistName);

        fillAlbumArts(jsonObject, album);

        return album;
    }

    @Override
    protected String[] getResultsStatisticObjectPath() throws JSONException {
        return new String[]{"topalbums","@attr"};
    }
}
