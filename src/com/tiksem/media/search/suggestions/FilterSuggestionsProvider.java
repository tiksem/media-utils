package com.tiksem.media.search.suggestions;

import com.utils.framework.algorithms.Search;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 19:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class FilterSuggestionsProvider implements SuggestionsProvider{
    protected abstract List getItemsToFilter();

    protected int getMaxCount(){
        return Integer.MAX_VALUE;
    }

    @Override
    public List<String> getSuggestions(String query) {
        List items = getItemsToFilter();
        return Search.filter(items, query, getMaxCount());
    }
}
