package com.tiksem.media.data;

import com.utils.framework.collections.cache.GlobalStringCache;

public class Album extends ArtCollection{
    private static final GlobalStringCache STRING_CACHE = GlobalStringCache.getInstance();

    private String artistName;
    private Object artistId;
    private String mbid;

    private Album(int id, boolean local) {
        super(id, local);
    }

    private Album(boolean local) {
        super(local);
    }

    public static Album createLocalAlbum(int id){
        return new Album(id,true);
    }

    public static Album createInternetAlbum(int id){
        return new Album(id,false);
    }

    public static Album createLocalAlbum(){
        return new Album(true);
    }

    public static Album createInternetAlbum(){
        return new Album(false);
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = STRING_CACHE.putOrGet(artistName);
    }

    public Object getArtistId() {
        return artistId;
    }

    public void setArtistId(Object artistId) {
        this.artistId = artistId;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }
}
