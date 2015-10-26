package com.tiksem.media.data;

import android.os.Parcel;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.artistName);
        dest.writeLong(this.artistId);
        dest.writeString(this.mbid);
    }

    protected Album(Parcel in) {
        super(in);
        this.artistName = in.readString();
        this.artistId = in.readLong();
        this.mbid = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
