package com.tiksem.media.search.suggestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */
public final class SuggestionsProviderFactory {
    public static SuggestionsProvider merge(final Iterable<SuggestionsProvider> providers){
        return new SuggestionsProvider() {
            @Override
            public List<String> getSuggestions(String query) {
                List<String> suggestions = new ArrayList<String>();
                for(SuggestionsProvider provider : providers){
                    suggestions.addAll(provider.getSuggestions(query));
                }

                return suggestions;
            }
        };
    }

    public static SuggestionsProvider merge(SuggestionsProvider... providers){
        return merge(Arrays.asList(providers));
    }
}
