package com.tiksem.media.search.navigation;

import android.os.AsyncTask;
import android.os.Handler;
import com.tiksem.media.search.SearchResult;
import com.utils.framework.OnError;
import com.utils.framework.collections.NavigationList;
import com.utils.framework.collections.OnLoadingFinished;
import com.utilsframework.android.ErrorListener;

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
public abstract class AsyncNavigationList<T> extends NavigationList<T> {
    private ErrorListener errorListener;

    protected AsyncNavigationList(List<T> initialElements,
                                  int maxElementsCount)
    {
        super(initialElements, maxElementsCount);
    }

    @Override
    public void getElementsOfPage(final int pageNumber,
                                  final OnLoadingFinished<T> onPageLoadingFinished, OnError onError)
    {
        new AsyncTask<Void, Void, SearchResult<T>>(){
            @Override
            protected void onPostExecute(SearchResult<T> searchResult) {
                if (searchResult != null) {
                    onPageLoadingFinished.onLoadingFinished(searchResult.elements, searchResult.isLastPage);
                }
            }

            @Override
            protected SearchResult<T> doInBackground(Void... params) {
                try {
                    if(isAllDataLoaded()){
                        return null;
                    }
                    SearchResult<T> result = search(pageNumber);
                    if(result.elements == null){
                        throw new NullPointerException();
                    }
                    return result;
                }
                catch (Exception e) {
                    if(errorListener != null){
                        errorListener.onError(e);
                    }

                    SearchResult<T> searchResult = new SearchResult<T>();
                    searchResult.elements = Collections.emptyList();
                    searchResult.isLastPage = true;
                    return searchResult;
                }


            }
        }.execute();
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    protected abstract SearchResult<T> search(int pageNumber) throws Exception;
}
