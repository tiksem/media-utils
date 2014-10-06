package com.tiksem.media.data;

import com.utils.framework.collections.cache.GlobalStringCache;

public class Album extends ArtCollection{
    private static final GlobalStringCache STRING_CACHE = GlobalStringCache.getInstance();

    private String artistName;
    private long artistId = -1;
    private String mbid;

    private Album(long id, boolean local) {
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

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Album album = (Album) o;

        if (artistId != album.artistId) return false;
        if (artistName != null ? !artistName.equals(album.artistName) : album.artistName != null) return false;
        if (mbid != null ? !mbid.equals(album.mbid) : album.mbid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (artistName != null ? artistName.hashCode() : 0);
        result = 31 * result + (int) (artistId ^ (artistId >>> 32));
        result = 31 * result + (mbid != null ? mbid.hashCode() : 0);
        return result;
    }
}
