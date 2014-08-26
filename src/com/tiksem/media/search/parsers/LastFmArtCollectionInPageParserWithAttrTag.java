package com.tiksem.media.search.parsers;

import com.tiksem.media.data.ArtCollection;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 24.02.13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
abstract class LastFmArtCollectionInPageParserWithAttrTag<T extends ArtCollection>
        extends LastFmArtCollectionInPageParser<T>
{
    @Override
    protected String getItemsPerPageFiledName() throws JSONException {
        return "perPage";
    }

    @Override
    protected String getTotalResultsFiledName() throws JSONException {
        return "total";
    }

    @Override
    protected int getStartIndex(String filedName, JSONObject statisticsObject) throws JSONException {
        int page = statisticsObject.getInt("page") - 1;
        int perPage = getItemsPerPage(getItemsPerPageFiledName(), statisticsObject);

        return page * perPage;
    }
}
