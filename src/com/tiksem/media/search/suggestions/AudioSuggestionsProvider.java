package com.tiksem.media.search.suggestions;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class AudioSuggestionsProvider extends SuggestionsProviderWithHelpWord{
    @Override
    protected String getHelpWord() {
        return "lyrics";
    }
}
