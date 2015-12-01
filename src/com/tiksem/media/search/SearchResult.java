package com.tiksem.media.search;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 19.01.13
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */

public final class SearchResult<T> implements Serializable{
    public List<T> elements;
    public boolean isLastPage = false;

    public static <T> SearchResult<T> empty(){
        SearchResult<T> searchResult = new SearchResult<T>();
        searchResult.elements = Collections.emptyList();
        searchResult.isLastPage = true;
        return searchResult;
    }

    public static <T> SearchResult<T> single(T item) {
        SearchResult<T> searchResult = new SearchResult<T>();
        searchResult.elements = Collections.singletonList(item);
        searchResult.isLastPage = false;
        return searchResult;
    }
}
