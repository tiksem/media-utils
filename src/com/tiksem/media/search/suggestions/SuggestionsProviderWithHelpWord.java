package com.tiksem.media.search.suggestions;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class SuggestionsProviderWithHelpWord extends GoogleSuggestionsProvider{
    private String lastHelpWorld;

    protected abstract String getHelpWord();

    private void removeHelpWordsFromSuggestions(List<String> suggestions){
        String replacement = " *" + lastHelpWorld + " *";

        for (int i = 0; i < suggestions.size(); i++) {
            String suggestion = suggestions.get(i);
            suggestion = suggestion.replaceAll(replacement,"");

            suggestions.set(i,suggestion);
        }
    }

    @Override
    public List<String> getSuggestions(String query) {
        if (query.equals("")) {
           return Collections.emptyList();
        }

        lastHelpWorld = getHelpWord();
        query = lastHelpWorld + " " + query;

        List<String> suggestions = super.getSuggestions(query);
        removeHelpWordsFromSuggestions(suggestions);
        return suggestions;
    }
}
