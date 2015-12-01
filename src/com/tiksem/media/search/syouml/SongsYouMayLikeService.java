package com.tiksem.media.search.syouml;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.utils.framework.collections.NavigationList;
import com.utils.framework.network.RequestExecutor;
import com.utilsframework.android.Services;
import com.utilsframework.android.network.AsyncRequestExecutorManager;
import com.utilsframework.android.network.RequestManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by stykhonenko on 28.10.15.
 */
public abstract class SongsYouMayLikeService extends Service {
    private static final int MAX_SONGS_YOU_MAY_LIKE_COUNT = 3000;
    private static final int SIMILAR_TRACKS_PER_PAGE_COUNT = 100;
    private SongsYouMayLikeNavigationList navigationList;
    private ExecutorService executor;
    private RequestManager requestManager;

    public class Binder extends android.os.Binder implements Services.OnUnbind {
        public NavigationList<Audio> getSongsYouMayLike() {
            if (navigationList == null) {
                SongsYouMayLikeNavigationList.Params params = new SongsYouMayLikeNavigationList.Params();
                params.internetSearchEngine = new InternetSearchEngine(createRequestExecutor());
                params.maxCount = MAX_SONGS_YOU_MAY_LIKE_COUNT;
                params.songsCountPerPage = SIMILAR_TRACKS_PER_PAGE_COUNT;
                params.userPlaylist = getUserPlayList();

                if (executor == null) {
                    executor = Executors.newSingleThreadExecutor();
                }
                if (requestManager == null) {
                    requestManager = new AsyncRequestExecutorManager(executor);
                } else {
                    requestManager.cancelAll();
                }
                params.requestManager = requestManager;

                navigationList = new SongsYouMayLikeNavigationList(params);
            }

            return navigationList;
        }

        @Override
        public void onUnbind() {
            if (navigationList != null) {
                continueLoadingOnBackground();
            }
        }

        public void continueLoadingOnBackground() {
            navigationList.loadNextPage(new NavigationList.OnPageLoadingFinished<Audio>() {
                @Override
                public void onLoadingFinished(List<Audio> elements) {
                    if (!navigationList.isAllDataLoaded()) {
                        navigationList.loadNextPage();
                    }
                }
            });
        }

        public NavigationList<Audio> reload() {
            if (navigationList != null) {
                navigationList.forceAllDataLoaded();
                navigationList = null;
            }

            return getSongsYouMayLike();
        }
    }

    public static void bindAndStart(Context context, Class<? extends SongsYouMayLikeService> aClass,
                                    Services.OnBind<Binder> onBind) {
        Services.start(context, aClass);
        Services.bind(context, aClass, onBind);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestManager != null) {
            requestManager.cancelAll();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    protected abstract RequestExecutor createRequestExecutor();
    protected abstract List<Audio> getUserPlayList();
}
