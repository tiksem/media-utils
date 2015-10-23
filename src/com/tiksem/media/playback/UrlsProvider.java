package com.tiksem.media.playback;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 23.10.15.
 */
public interface UrlsProvider {
    List<String> getUrls() throws IOException;
}
