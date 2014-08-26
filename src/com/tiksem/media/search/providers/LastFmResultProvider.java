package com.tiksem.media.search.providers;

import com.tiksem.media.search.network.LastFMSearchParams;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 3/13/13
 * Time: 4:16 PM
 */
public abstract class LastFmResultProvider<T> implements ResultProvider<T>{
    protected LastFMSearchParams searchParams;

    protected LastFmResultProvider(LastFMSearchParams searchParams) {
        this.searchParams = searchParams;
    }
}
