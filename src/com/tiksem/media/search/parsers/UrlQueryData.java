package com.tiksem.media.search.parsers;

import com.utils.framework.collections.cache.GlobalStringCache;

/**
 * Created by stykhonenko on 27.10.15.
 */
public class UrlQueryData {
    private static final GlobalStringCache GLOBAL_STRING_CACHE = GlobalStringCache.getInstance();

    private String url;
    private int duration;
    private String name;
    private String artistName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = GLOBAL_STRING_CACHE.putOrGet(artistName);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
