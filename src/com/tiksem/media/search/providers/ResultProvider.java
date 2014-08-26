package com.tiksem.media.search.providers;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 3/13/13
 * Time: 4:15 PM
 */
public interface ResultProvider<T> {
    String search();
    T parse(String response);
}
