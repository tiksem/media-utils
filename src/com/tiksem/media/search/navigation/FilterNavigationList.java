package com.tiksem.media.search.navigation;

import com.tiksem.media.search.queue.SearchQueue;
import com.utils.framework.OnError;
import com.utils.framework.collections.EmptyList;
import com.utils.framework.collections.NavigationList;
import com.utils.framework.collections.OnLoadingFinished;
import com.utilsframework.android.network.RequestManager;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class FilterNavigationList<T> extends NavigationList<T> {
    private static final EmptyList LAST_PAGE = new EmptyList();

    private RequestManager requestManager;
    private SearchQueue<T> searchQueue;

    public FilterNavigationList(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public void getElementsOfPage(int pageNumber, final OnLoadingFinished<T> onPageLoadingFinished,
                                  final OnError onError) {
        requestManager.execute(new Threading.Task<IOException, List<T>>() {
            @Override
            public List<T> runOnBackground() throws IOException {
                T item = searchQueue.get();
                if (item == null) {
                    return LAST_PAGE;
                }

                if (satisfies(item)) {
                    return Collections.singletonList(item);
                }

                return Collections.emptyList();
            }

            @Override
            public void onComplete(List<T> elements, IOException error) {
                if (error == null) {
                    onPageLoadingFinished.onLoadingFinished(elements, elements == LAST_PAGE);
                } else {
                    onError.onError(error);
                }
            }
        });
    }

    protected final void setSearchQueue(SearchQueue<T> searchQueue) {
        this.searchQueue = searchQueue;
    }

    protected final SearchQueue<T> getSearchQueue() {
        return searchQueue;
    }

    protected abstract boolean satisfies(T item) throws IOException;
}
