package com.tiksem.media.search.queue;

import com.tiksem.media.search.SearchResult;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by stykhonenko on 05.11.15.
 */
public abstract class SearchQueue<T> {
    private ArrayDeque<T> elements = new ArrayDeque<>();
    private int pageNumber = 1;
    private boolean isLastPage;

    public T get() throws IOException {
        if (isLastPage) {
            return null;
        }

        while (elements.isEmpty() && !isLastPage) {
            SearchResult<T> searchResult = loadData(pageNumber++);
            boolean empty = searchResult.elements.isEmpty();
            isLastPage = searchResult.isLastPage || empty;
            elements.addAll(searchResult.elements);
        }

        return elements.poll();
    }

    protected abstract SearchResult<T> loadData(int pageNumber) throws IOException;
}
