package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private List<String> getAudioUrls(JSONArray tracks) throws JSONException {
        ArrayList<String> urls = new ArrayList<String>(tracks.length() - 1);
        //first array element - results count
        for(int i = 1; i < tracks.length(); i++){
            JSONObject track = tracks.getJSONObject(i);
            String url = track.getString("url");
            urls.add(url);
        }

        return urls;
    }

    public List<String> getAudioUrls(String response) throws JSONException {
        JSONArray tracks = getAudiosJSONArray(response);
        return getAudioUrls(tracks);
    }

    public List<String> getAudioUrls(String response, Audio audio) throws JSONException
    {
        JSONArray tracks = getAudiosJSONArray(response);
        List<String> urls = getAudioUrls(tracks);

        VkAudioUrlPriorityProvider priorityProvider =
                new VkAudioUrlPriorityProvider(tracks, audio);
        CollectionUtils.sortByPriority(urls, priorityProvider);

        return urls;
    }
}
