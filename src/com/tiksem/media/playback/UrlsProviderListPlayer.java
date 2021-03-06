package com.tiksem.media.playback;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import com.utilsframework.android.network.RequestManager;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.List;

/**
 * Created by stykhonenko on 23.10.15.
 */
public class UrlsProviderListPlayer extends Player<UrlsProvider> {
    private List<UrlsProvider> providers;
    private List<String> urls;
    private int urlPosition;
    private RequestManager requestManager;
    private AsyncTask urlsGettingTask;

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
            urlsGettingTask = requestManager.execute(new Threading.Task<IOException, List<String>>() {
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
                            return;
                        }

                        urls = urlList;
                        urlPosition = 0;
                        getCurrentUrl(onUrlReady);
                    } else {
                        onError(null);
                        playNext();
                    }
                }

                @Override
                public void onAfterCompleteOrCancelled() {
                    urlsGettingTask = null;
                }
            });
        }
    }

    @Override
    public void reset() {
        super.reset();

        urls = null;
        urlPosition = 0;

        cancelLastTaskIfNeed();
    }

    private void cancelLastTaskIfNeed() {
        if (urlsGettingTask != null) {
            urlsGettingTask.cancel(true);
            urlsGettingTask = null;
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
    protected boolean canGoToNextUrl() {
        return urlPosition < urls.size() - 1;
    }

    public List<UrlsProvider> getProviders() {
        return providers;
    }

    public int getUrlPosition() {
        return urlPosition;
    }

    @Override
    public String getCurrentUrl() {
        if (urls == null) {
            throw new IllegalStateException("getCurrentUrl can only be called when song is selected");
        }

        return urls.get(urlPosition);
    }

    @Override
    public List<UrlsProvider> getPlayList() {
        return providers;
    }

    @Override
    protected void setPlayList(List<UrlsProvider> newPlayList) {
        providers = newPlayList;
    }

    @Override
    protected void tryPlayCurrentUrl() {
        cancelLastTaskIfNeed();
        super.tryPlayCurrentUrl();
    }
}
