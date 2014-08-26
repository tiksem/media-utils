package com.tiksem.media.search.network;

import com.utils.framework.io.TextLoader;

import java.io.IOException;
import java.util.Map;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/6/13
 * Time: 6:06 PM
 */
public class VkSearcher {
    private static final String ACCESS_TOKEN = "c5f5835e25dd3d1ec49076302f7a85916ae6acc2e5dae83c90100ced7c199c557d4a8aaeeb08922fbd4f4";
    private static final String ROOT_URL = "https://api.vk.com/method/";
    private static final TextLoader TEXT_LOADER = TextLoader.getInstance();

    private String executeMethod(String methodName, Map<String,Object> params) throws IOException {
        String url = ROOT_URL + methodName;
        params.put("access_token", ACCESS_TOKEN);
        return TEXT_LOADER.getTextFromUrl(url, params);
    }

    public String searchAudios(String query, VkAudioSearchParams params) throws IOException{
        Map<String,Object> paramsMap = params.toMap();
        if(!query.isEmpty()){
            paramsMap.put("q",query);
        }
        return executeMethod("audio.search",paramsMap);
    }

    public String searchAudios(String query) throws IOException {
        return searchAudios(query, new VkAudioSearchParams());
    }
}
