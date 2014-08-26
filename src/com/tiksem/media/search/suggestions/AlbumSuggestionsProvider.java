package com.tiksem.media.search.suggestions;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.02.13
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class AlbumSuggestionsProvider extends SuggestionsProviderWithHelpWord{
    @Override
    protected String getHelpWord() {
        return "album";
    }
}
