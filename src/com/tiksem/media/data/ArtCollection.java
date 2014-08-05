package com.tiksem.media.data;

import com.utils.framework.collections.cache.GlobalStringCache;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 27.10.12
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class ArtCollection extends NamedData implements Serializable{
    private static final GlobalStringCache STRING_CACHE = GlobalStringCache.getInstance();

    private String[] arts = new String[ArtSize.values().length];

    public ArtCollection(int id, boolean local){
        super(local,id);
    }

    public ArtCollection(boolean local){
        super(local);
    }

    public String getArtUrl(ArtSize size){
        int index = size.ordinal();
        return arts[index];
    }

    public final void setArtUrl(ArtSize size, String url){
        arts[size.ordinal()] = STRING_CACHE.putOrGet(url);
    }

    public final void setUrlForAllArts(String url){
        for(ArtSize artSize : ArtSize.values()){
            setArtUrl(artSize, url);
        }
    }

    public void cloneArtUrlsFrom(ArtCollection artCollection){
        arts = artCollection.arts.clone();
    }
}
