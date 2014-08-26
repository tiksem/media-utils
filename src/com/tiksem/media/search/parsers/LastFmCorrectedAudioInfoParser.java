package com.tiksem.media.search.parsers;

import com.utilsframework.android.parsers.json.JsonArrayElementParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 07.07.13
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
public class LastFmCorrectedAudioInfoParser extends JsonArrayElementParser<LastFmCorrectedAudioInfoParser.Info> {
    private static final int MAX_ELEMENTS_COUNT = 4;

    public static class Info{
        public String artistName;
        public String name;
        public int listenersCount;
    }

    @Override
    public Info parse(JSONObject jsonObject) throws JSONException {
        Info info = new Info();
        info.name = jsonObject.getString("name");
        info.artistName = jsonObject.getString("artist");
        info.listenersCount = jsonObject.optInt("listeners", 0);
        return info;
    }

    @Override
    protected int getElementsCount(JSONObject root, JSONArray array) throws JSONException {
        return MAX_ELEMENTS_COUNT;
    }
}
