package com.tiksem.media.search.network;

import com.utils.framework.network.RequestExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stykhonenko on 26.10.15.
 */
public class TheAudioDbSearcher {
    private static final String API_KEY = "1635249876521398675129";
    private static final String SEARCH_TRACK = "http://www.theaudiodb.com/api/v1/json/" + API_KEY +
            "/searchtrack.php";

    private RequestExecutor requestExecutor;

    public TheAudioDbSearcher(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    private String search(String url, Map<String, Object> args) throws IOException {
        return requestExecutor.executeRequest(url, args);
    }

    public String searchTrack(String name, String artistName) throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("s", artistName);
        args.put("t", name);
        return search(SEARCH_TRACK, args);
    }
}
