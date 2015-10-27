package com.tiksem.media.playback;

import android.media.MediaPlayer;
import com.utilsframework.android.network.RequestManager;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by stykhonenko on 23.10.15.
 */
public class UrlsProviderListPlayer extends Player {
    private List<UrlsProvider> providers;
    private List<String> urls;
    private int urlPosition;
    private RequestManager requestManager;

    public UrlsProviderListPlayer(MediaPlayer mediaPlayer, RequestManager requestManager,
                                  List<UrlsProvider> providers) {
        super(mediaPlayer);
        this.requestManager = requestManager;
        this.providers = providers;
    }

    @Override
    protected void getCurrentUrl(final OnUrlReady onUrlReady) {
        if (urls != null) {
            onUrlReady.onUrlReady(urls.get(urlPosition));
        } else {
            final UrlsProvider provider = providers.get(getPosition());
            requestManager.execute(new Threading.Task<IOException, List<String>>() {
                @Override
                public List<String> runOnBackground() throws IOException {
                    return provider.getUrls();
                }

                @Override
                public void onComplete(List<String> urlList, IOException error) {
                    if (error == null) {
                        if (urlList.isEmpty()) {
                            onError(null);
                            playNext();
                        }

                        urls = urlList;
                        urlPosition = 0;
                        getCurrentUrl(onUrlReady);
                    } else {
                        onError(null);
                        playNext();
                    }
                }
            });
        }
    }

    @Override
    protected int getPlayListSize() {
        return providers.size();
    }

    @Override
    protected void goToNextUrl() {
        urlPosition++;
    }

    @Override
    protected boolean supportsSeveralUrlsForAudio() {
        return true;
    }

    @Override
    protected boolean canGoToNextUrl() {
        return urlPosition < urls.size() - 1;
    }

    @Override
    protected void onPositionChanged() {
        super.onPositionChanged();
        urls = null;
        urlPosition = 0;
    }
}
