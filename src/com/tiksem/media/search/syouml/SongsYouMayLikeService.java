package com.tiksem.media.search.syouml;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.songs.TopSongsByGenresNavigationList;
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
    private static final int TRACKS_BY_GENRE_PER_PAGE_COUNT = 30;

    private NavigationList<Audio> navigationList;
    private ExecutorService executor;
    private RequestManager requestManager;
    private boolean searchSongsByGenres = false;

    public class Binder extends android.os.Binder implements Services.OnUnbind {
        public NavigationList<Audio> getSuggestedSongs() {
            if (navigationList == null) {
                if (executor == null) {
                    executor = Executors.newSingleThreadExecutor();
                }
                if (requestManager == null) {
                    requestManager = new AsyncRequestExecutorManager(executor);
                } else {
                    requestManager.cancelAll();
                }
                InternetSearchEngine internetSearchEngine = new InternetSearchEngine(createRequestExecutor());

                if (!searchSongsByGenres) {
                    SongsYouMayLikeNavigationList.Params params = new SongsYouMayLikeNavigationList.Params();
                    params.internetSearchEngine = internetSearchEngine;
                    params.maxCount = MAX_SONGS_YOU_MAY_LIKE_COUNT;
                    params.songsCountPerPage = SIMILAR_TRACKS_PER_PAGE_COUNT;
                    params.userPlaylist = getUserPlayList();
                    params.requestManager = requestManager;

                    navigationList = new SongsYouMayLikeNavigationList(params);
                } else {
                    navigationList = new TopSongsByGenresNavigationList(requestManager,
                            internetSearchEngine, TRACKS_BY_GENRE_PER_PAGE_COUNT, MAX_SONGS_YOU_MAY_LIKE_COUNT);
                }
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

            return getSuggestedSongs();
        }

        public void setSearchSongsByGenres(boolean value) {
            searchSongsByGenres = value;
        }

        public boolean searchSongsByGenres() {
            return searchSongsByGenres;
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
