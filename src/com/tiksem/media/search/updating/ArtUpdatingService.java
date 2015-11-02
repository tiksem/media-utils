package com.tiksem.media.search.updating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.tiksem.media.data.*;
import com.tiksem.media.local.FlyingDogAudioDatabase;
import com.tiksem.media.search.InternetSearchEngine;
import com.utils.framework.network.RequestExecutor;
import com.utilsframework.android.network.AsyncRequestExecutorManager;
import com.utilsframework.android.network.RequestManager;
import com.utilsframework.android.threading.OnFinish;
import com.utilsframework.android.threading.Threading;
import com.utilsframework.android.threading.ThrowingRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by stykhonenko on 30.10.15.
 */
public abstract class ArtUpdatingService extends Service {
    private static final String UPDATE_ARTS_ACTION = "UPDATE_ARTS_ACTION";

    private ExecutorService executor;
    private RequestManager requestManager;
    private InternetSearchEngine internetSearchEngine;
    private FlyingDogAudioDatabase audioDatabase;
    private boolean updateExecuted;

    @Override
    public void onCreate() {
        super.onCreate();

        audioDatabase = getAudioDatabase();
        internetSearchEngine = new InternetSearchEngine(getRequestExecutor());
        executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
        });
        requestManager = new AsyncRequestExecutorManager(executor);
    }

    protected static void updateAudioArts(Context context, Class<? extends ArtUpdatingService> aClass) {
        Intent intent = new Intent(context, aClass);
        intent.setAction(UPDATE_ARTS_ACTION);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (UPDATE_ARTS_ACTION.equals(action)) {
            startArtsUpdating();
        }

        return START_REDELIVER_INTENT;
    }

    private void startArtsUpdating() {
        if (updateExecuted) {
            return;
        }

        updateExecuted = true;

        requestManager.execute(new ThrowingRunnable<IOException>() {
            @Override
            public void run() throws IOException {
                updateAudioArts();
                updateArtistArts();
                updateAlbumArts();
            }
        }, new OnFinish<IOException>() {
            @Override
            public void onFinish(IOException e) {
                updateExecuted = false;
                stopSelf();
            }
        });
    }

    private interface Updater<T extends ArtCollection> {
        ArtCollection getArts(T item) throws IOException;
        void save(String artUrl, ArtSize artSize, T item) throws IOException;
    }


    private <T extends ArtCollection> void updateArts(List<T> items, Updater<T> updater) {
        for (T item : items) {
            if (item.getArtUrl(ArtSize.SMALL) == null) {
                try {
                    ArtCollection arts = updater.getArts(item);
                    if (arts != null) {
                        for (ArtSize artSize : ArtSize.values()) {
                            String artUrl = arts.getArtUrl(artSize);
                            updater.save(artUrl, artSize, item);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateAudioArts() {
        updateArts(audioDatabase.getSongs(), new Updater<Audio>() {
            @Override
            public ArtCollection getArts(Audio item) throws IOException {
                return internetSearchEngine.getArts(item);
            }

            @Override
            public void save(String artUrl, ArtSize artSize, Audio item) throws IOException {
                audioDatabase.downloadAndSaveAudioArt(item, artUrl, artSize);
            }
        });
    }

    private void updateArtistArts() {
        List<Artist> artists = audioDatabase.getArtists();

        updateArts(artists, new Updater<Artist>() {
            @Override
            public ArtCollection getArts(Artist item) throws IOException {
                return internetSearchEngine.getArts(item);
            }

            @Override
            public void save(String artUrl, ArtSize artSize, Artist item) throws IOException {
                audioDatabase.downloadAndSaveArtistArt(item, artUrl, artSize);
            }
        });
    }

    private void updateAlbumArts() {
        List<Album> albums = audioDatabase.getAlbums();

        updateArts(albums, new Updater<Album>() {
            @Override
            public ArtCollection getArts(Album item) throws IOException {
                return internetSearchEngine.getArts(item);
            }

            @Override
            public void save(String artUrl, ArtSize artSize, Album item) throws IOException {
                audioDatabase.downloadAndSaveAlbumArt(item, artUrl, artSize);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    protected abstract RequestExecutor getRequestExecutor();
    protected abstract FlyingDogAudioDatabase getAudioDatabase();
}
