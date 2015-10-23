package com.tiksem.media.search.network;

import com.utils.framework.io.TextLoader;
import com.utils.framework.network.RequestExecutor;

import java.io.IOException;
import java.util.Map;

/**
 * Project: FlyingDog
 * User: stikhonenko
 * Date: 2/6/13
 * Time: 6:06 PM
 */
public class VkSearcher {
    private static final String ACCESS_TOKEN = "9c97dd8c8516b4412ccd36543491ec399b9f95505c39a75e722ba0efe38cf54e1cab52045fc83144fa461";
    private static final String ROOT_URL = "https://api.vk.com/method/";
    private RequestExecutor requestExecutor;

    private String executeMethod(String methodName, Map<String,Object> params) throws IOException {
        String url = ROOT_URL + methodName;
        params.put("access_token", ACCESS_TOKEN);
        return requestExecutor.executeRequest(url, params);
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

    public VkSearcher(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }
}
