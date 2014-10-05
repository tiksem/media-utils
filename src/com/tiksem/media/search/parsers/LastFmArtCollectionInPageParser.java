package com.tiksem.media.search.parsers;

import com.utils.framework.parsers.json.ExtendedJSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/14/13
 * Time: 8:13 PM
 */
abstract class LastFmArtCollectionInPageParser<T> extends LastFmArtCollectionParser<T>{
    private boolean isLastPage;

    public boolean isLastPage() {
        return isLastPage;
    }

    protected final void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    private Integer getResponseOutOfRange(JSONObject responseObject) throws JSONException{
        String[] statisticsPath = getResultsStatisticObjectPath();
        JSONObject statistics = ExtendedJSONObject.getJsonObjectFromPath(responseObject, statisticsPath);

        int totalResults = getTotalResults(getTotalResultsFiledName(), statistics);
        int startIndex = getStartIndex(getStartIndexFieldName(), statistics);
        int itemsPerPage = getItemsPerPage(getItemsPerPageFiledName(), statistics);

        int result = startIndex - totalResults + itemsPerPage;
        if(result >= itemsPerPage){
            return null;
        }
        return result;
    }

    protected String getStartIndexFieldName() throws JSONException {
        return "opensearch:startIndex";
    }

    protected String getItemsPerPageFiledName() throws JSONException {
        return "opensearch:itemsPerPage";
    }

    protected String getTotalResultsFiledName() throws JSONException {
        return "opensearch:totalResults";
    }

    protected int getTotalResults(String filedName, JSONObject statisticsObject) throws JSONException {
        return statisticsObject.getInt(filedName);
    }

    protected int getStartIndex(String filedName, JSONObject statisticsObject) throws JSONException {
        return statisticsObject.getInt(filedName);
    }

    protected int getItemsPerPage(String filedName, JSONObject statisticsObject) throws JSONException {
        return statisticsObject.getInt(filedName);
    }

    protected String[] getResultsStatisticObjectPath() throws JSONException {
        return new String[]{"results"};
    }

    private boolean checkResponseForOutOfRange(JSONObject responseObject) throws JSONException{
        return getResponseOutOfRange(responseObject) <= 0;
    }

    @Override
    public int getElementsCount(JSONObject responseObject, JSONArray objects)
            throws JSONException
    {
        isLastPage = false;
        Integer offset = getResponseOutOfRange(responseObject);
        int elementsCount = objects.length();
        if(offset != null){
            if(offset < 0){
                return elementsCount;
            }
        } else {
            return 0;
        }

        isLastPage = true;
        return elementsCount;
    }
}
