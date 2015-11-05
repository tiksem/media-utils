package com.tiksem.media.search.navigation;

import com.tiksem.media.search.InternetSearchEngine;
import com.utilsframework.android.network.RequestManager;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class PageNavListParams {
    public InternetSearchEngine internetSearchEngine;
    public int elementsOfPageCount = 50;
    public String query;
    public RequestManager requestManager;
}
