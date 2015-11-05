package com.tiksem.media.search.navigation;

import com.tiksem.media.search.InternetSearchEngine;
import com.utilsframework.android.network.RequestManager;

/**
 * Created by stykhonenko on 05.11.15.
 */
public class FilterNavigationListParams {
    public RequestManager requestManager;
    public InternetSearchEngine internetSearchEngine;
    public String query;
    public int itemsPerPage = 50;
}
