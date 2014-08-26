package com.tiksem.media.data;

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
    transient private float totalListeningDuration = 1;

    private Audio(boolean local) {
        super(local);
    }

    private Audio(boolean local, int id) {
        super(id, local);
    }

    public static Audio createLocalAudio(int id){
        return new Audio(true,id);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Audio audio = (Audio) o;

        if (albumId != audio.albumId) return false;
        if (artistId != audio.artistId) return false;
        if (duration != audio.duration) return false;
        if (Float.compare(audio.totalListeningDuration, totalListeningDuration) != 0) return false;
        if (albumName != null ? !albumName.equals(audio.albumName) : audio.albumName != null) return false;
        if (artistName != null ? !artistName.equals(audio.artistName) : audio.artistName != null) return false;
        if (lyrics != null ? !lyrics.equals(audio.lyrics) : audio.lyrics != null) return false;
        if (url != null ? !url.equals(audio.url) : audio.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (lyrics != null ? lyrics.hashCode() : 0);
        result = 31 * result + (albumName != null ? albumName.hashCode() : 0);
        result = 31 * result + (int) (albumId ^ (albumId >>> 32));
        result = 31 * result + (artistName != null ? artistName.hashCode() : 0);
        result = 31 * result + (int) (artistId ^ (artistId >>> 32));
        result = 31 * result + duration;
        result = 31 * result + (totalListeningDuration != +0.0f ? Float.floatToIntBits(totalListeningDuration) : 0);
        return result;
    }
}