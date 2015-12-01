package com.tiksem.media.search.navigation;

import android.os.AsyncTask;
import android.os.Handler;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.OnError;
import com.utils.framework.collections.NavigationList;
import com.utils.framework.collections.OnLoadingFinished;
import com.utils.framework.collections.UniqueNavigationList;
import com.utilsframework.android.ErrorListener;
import com.utilsframework.android.network.RequestManager;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 06.01.13
 * Time: 20:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AsyncNavigationList<T> extends UniqueNavigationList<T> {
    private ErrorListener errorListener;
    private RequestManager requestManager;

    protected AsyncNavigationList(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    protected AsyncNavigationList(RequestManager requestManager, int maxCount) {
        super(maxCount);
        if (requestManager == null) {
            throw new NullPointerException("requestManager == null");
        }

        this.requestManager = requestManager;
    }

    @Override
    public void getElementsOfPage(final int pageNumber,
                                  final OnLoadingFinished<T> onPageLoadingFinished, final OnError onError) {
        requestManager.execute(new Threading.Task<IOException, SearchResult<T>>() {
            @Override
            public SearchResult<T> runOnBackground() throws IOException {
                if (isAllDataLoaded()) {
                    return null;
                }
                SearchResult<T> result = search(pageNumber);
                if (result.elements == null) {
                    throw new NullPointerException();
                }
                return result;
            }

            @Override
            public void onComplete(SearchResult<T> searchResult, IOException error) {
                if (searchResult != null) {
                    onPageLoadingFinished.onLoadingFinished(searchResult.elements, searchResult.isLastPage);
                } else {
                    if (errorListener != null) {
                        errorListener.onError(error);
                    }

                    if (onError != null) {
                        onError.onError(error);
                    }
                }
            }
        });
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    protected abstract SearchResult<T> search(int pageNumber) throws IOException;
}
