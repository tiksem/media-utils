package com.tiksem.media.search.navigation;

import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 08.03.13
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class MultiTagNavigationList<T> extends AsyncNavigationList<T> {
    public static class Params{
        public int maxElementsCount = 1000;
        public int elementsPerPage = 25;
        public String[] tags;
        public InternetSearchEngine internetSearchEngine;
    }

    private String[] tags;
    private int elementsPerPage;
    private InternetSearchEngine internetSearchEngine;

    protected int getElementsPerPage() {
        return elementsPerPage;
    }

    protected InternetSearchEngine getInternetSearchEngine() {
        return internetSearchEngine;
    }

    public MultiTagNavigationList(Params params) {
        super(Collections.<T>emptyList(), params.maxElementsCount);

        elementsPerPage = params.elementsPerPage;
        tags = params.tags;
        internetSearchEngine = params.internetSearchEngine;
    }

    protected abstract SearchResult<T> searchByTag(String tag, int pageNumber) throws Exception;

    @Override
    protected SearchResult<T> search(int pageNumber) throws Exception {
        SearchResult<T> result = new SearchResult();
        result.isLastPage = true;
        List<List<T>> resultsByTag = new ArrayList(tags.length);

        for(String tag : tags){
            SearchResult<T> searchResultByTag =
                    searchByTag(tag, pageNumber);

            if(!searchResultByTag.isLastPage){
                result.isLastPage = false;
            }

            resultsByTag.add(searchResultByTag.elements);
        }

        result.elements = CollectionUtils.mergeToSingleListSequentially(resultsByTag);
        return result;
    }
}
