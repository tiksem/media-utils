package com.tiksem.media.search.navigation;

import com.tiksem.media.search.InternetSearchEngine;
import com.utilsframework.android.network.RequestManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 10.02.13
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class PageNavigationList<T> extends AsyncNavigationList<T> {
    private InternetSearchEngine internetSearchEngine;
    private String query;
    private int elementsOfPageCount;

    public static class InitParams {
        public InternetSearchEngine internetSearchEngine;
        public int elementsOfPageCount = 50;
        public String query;
        public RequestManager requestManager;
    }

    public PageNavigationList(InitParams initialParams)
    {
        super(initialParams.requestManager);
        this.internetSearchEngine = initialParams.internetSearchEngine;

        elementsOfPageCount = initialParams.elementsOfPageCount;
        if(elementsOfPageCount <= 0){
            throw new IllegalArgumentException();
        }

        query = initialParams.query;
        if(query == null){
            throw new NullPointerException();
        }
    }

    protected InternetSearchEngine getInternetSearchEngine() {
        return internetSearchEngine;
    }

    protected String getQuery() {
        return query;
    }

    protected int getElementsOfPageCount() {
        return elementsOfPageCount;
    }
}
