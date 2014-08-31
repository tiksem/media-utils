package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Album;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/14/13
 * Time: 8:24 PM
 */
class LastFmAlbumParser extends LastFmArtCollectionInPageParser<Album> {
    @Override
    public Album parse(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.optInt("id", -1);
        String mbid = jsonObject.optString("mbid");

        String name = jsonObject.optString("name", null);
        if(name == null){
            name = jsonObject.getString("title");
        }

        final String artistName = jsonObject.getString("artist");
        final Album album = Album.createInternetAlbum(id);

        if(!fillAlbumArts(jsonObject, album)){
            return null;
        }

        if(mbid != null && !mbid.isEmpty()){
            album.setMbid(mbid);
        }

        album.setName(name);
        album.setArtistName(artistName);

        return album;
    }
}
