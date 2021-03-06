package com.tiksem.media.data;

import android.os.Parcel;
import com.utils.framework.collections.cache.GlobalStringCache;

import java.util.Arrays;
import java.util.List;

public class Audio extends ArtCollection{
    public static final int DURATION_COEFFICIENT = 100000;
    private static final GlobalStringCache STRING_CACHE = GlobalStringCache.getInstance();

    private String url;
    transient private String lyrics;
    transient private String albumName;
    transient private long albumId;
    private String artistName;
    transient private long artistId;
    private int duration;
    private String mbid;
    private String lastFMUrl;
    transient private float totalListeningDuration = 1;

    private Audio(boolean local) {
        super(local);
    }

    private Audio(boolean local, long id) {
        super(id, local);
    }

    public Audio(Audio other) {
        super(other);
        this.url = other.url;
        this.lyrics = other.lyrics;
        this.albumName = other.albumName;
        this.albumId = other.albumId;
        this.artistName = other.artistName;
        this.artistId = other.artistId;
        this.duration = other.duration;
        this.totalListeningDuration = other.totalListeningDuration;
    }

    public void cloneDataFrom(Audio other) {
        super.cloneDataFrom(other);
        this.url = other.url;
        this.lyrics = other.lyrics;
        this.albumName = other.albumName;
        this.albumId = other.albumId;
        this.artistName = other.artistName;
        this.artistId = other.artistId;
        this.duration = other.duration;
        this.totalListeningDuration = other.totalListeningDuration;
    }

    public static Audio createLocalAudio(long id){
        return new Audio(true, id);
    }

    public static Audio createInternetAudio(int id){
        return new Audio(false,id);
    }

    public static Audio createLocalAudio(){
        return new Audio(true);
    }

    public static Audio createInternetAudio(){
        return new Audio(false);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = STRING_CACHE.putOrGet(artistName);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public float getTotalListeningDuration() {
        return totalListeningDuration;
    }

    public void setTotalListeningDuration(float totalListeningDuration) {
        this.totalListeningDuration = totalListeningDuration;
    }

    public void addListeningDurationWhilePlayBack(int value) {
        totalListeningDuration += value * DURATION_COEFFICIENT / (float)duration;
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
        dest.writeString(this.url);
        dest.writeString(this.lyrics);
        dest.writeString(this.albumName);
        dest.writeLong(this.albumId);
        dest.writeString(this.artistName);
        dest.writeLong(this.artistId);
        dest.writeInt(this.duration);
        dest.writeString(this.mbid);
        dest.writeFloat(this.totalListeningDuration);
    }

    protected Audio(Parcel in) {
        super(in);
        this.url = in.readString();
        this.lyrics = in.readString();
        this.albumName = in.readString();
        this.albumId = in.readLong();
        this.artistName = in.readString();
        this.artistId = in.readLong();
        this.duration = in.readInt();
        this.mbid = in.readString();
        this.totalListeningDuration = in.readFloat();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        public Audio createFromParcel(Parcel source) {
            return new Audio(source);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public String getLastFMUrl() {
        return lastFMUrl;
    }

    public void setLastFMUrl(String lastFMUrl) {
        this.lastFMUrl = lastFMUrl;
    }
}