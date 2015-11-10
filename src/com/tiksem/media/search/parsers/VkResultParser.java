package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/6/13
 * Time: 7:05 PM
 */

public class VkResultParser {
    private JSONArray getAudiosJSONArray(String response) throws JSONException {
        JSONObject responseJSONObject = new JSONObject(response);
        return responseJSONObject.getJSONArray("response");
    }

    private static class GetAudioUrlsResult{
        JSONArray tracks;
        List<String> urls;
    }

    private List<UrlQueryData> getAudioUrls(JSONArray tracks) throws JSONException {
        ArrayList<UrlQueryData> datas = new ArrayList<UrlQueryData>(tracks.length() - 1);
        //first array element - results count
        for(int i = 1; i < tracks.length(); i++){
            JSONObject track = tracks.getJSONObject(i);
            UrlQueryData data = new UrlQueryData();
            data.setUrl(track.getString("url"));
            data.setArtistName(track.optString("artist"));
            data.setName(track.optString("title"));
            data.setDuration(track.optInt("duration", -1));
            datas.add(data);
        }

        return datas;
    }

    public List<UrlQueryData> getAudioUrls(String response, Audio audio) throws JSONException
    {
        JSONArray tracks = getAudiosJSONArray(response);
        List<UrlQueryData> urlsData = getAudioUrls(tracks);

        VkAudioUrlPriorityProvider priorityProvider = new VkAudioUrlPriorityProvider(audio);
        CollectionUtils.sortByPriority(urlsData, priorityProvider);

        return urlsData;
    }
}
