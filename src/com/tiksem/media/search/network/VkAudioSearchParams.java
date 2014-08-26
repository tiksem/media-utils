package com.tiksem.media.search.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/6/13
 * Time: 6:27 PM
 */
public class VkAudioSearchParams {
    public static enum Sort{
        ADDING_DATE,
        POPULARITY,
        DURATION
    }

    private Sort sort;
    private Boolean lyrics;
    private Integer count;
    private Integer offset;

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Boolean getLyrics() {
        return lyrics;
    }

    public void setLyrics(Boolean lyrics) {
        this.lyrics = lyrics;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        if(count != null && count <= 0){
            throw new IllegalArgumentException();
        }

        this.count = count;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        if(offset != null && offset < 0){
            throw new IllegalArgumentException();
        }

        this.offset = offset;
    }

    public Map<String,Object> toMap() {
        Map<String,Object> result = new HashMap<String, Object>();

        if(sort != null){
            result.put("sort", sort.ordinal());
        }

        if(lyrics != null){
            result.put("lyrics", lyrics ? 1 : 0);
        }

        if(count != null){
            result.put("count",count);
        }

        if(offset != null){
            result.put("offset", offset);
        }

        return result;
    }
}
