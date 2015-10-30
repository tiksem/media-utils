package com.tiksem.media.search.updating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.tiksem.media.data.ArtCollection;
import com.tiksem.media.data.ArtSize;
import com.tiksem.media.data.Audio;
import com.tiksem.media.local.FlyingDogAudioDatabase;
import com.tiksem.media.search.InternetSearchEngine;
import com.utils.framework.network.RequestExecutor;
import com.utilsframework.android.network.AsyncRequestExecutorManager;
import com.utilsframework.android.network.RequestManager;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by stykhonenko on 30.10.15.
 */
public abstract class ArtUpdatingService extends Service {
    private static final String UPDATE_AUDIO_ARTS_ACTION = "UPDATE_AUDIO_ARTS_ACTION";

    private ExecutorService executor;
    private RequestManager requestManager;
    private InternetSearchEngine internetSearchEngine;
    private FlyingDogAudioDatabase audioDatabase;
    private int updatedAudiosCount = -1;

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
        intent.setAction(UPDATE_AUDIO_ARTS_ACTION);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (UPDATE_AUDIO_ARTS_ACTION.equals(action)) {
            startAudioArtsUpdating();
        }

        return START_REDELIVER_INTENT;
    }

    private void startAudioArtsUpdating() {
        if (updatedAudiosCount != -1) {
            // updating is already running
            return;
        }

        updatedAudiosCount = 0;

        List<Audio> songs = audioDatabase.getSongs();
        final int songsCount = songs.size();

        for (final Audio audio : songs) {
            if (audio.getArtUrl(ArtSize.SMALL) == null) {
                requestManager.execute(new Threading.Task<IOException, ArtCollection>() {
                    @Override
                    public ArtCollection runOnBackground() throws IOException {
                        ArtCollection arts = internetSearchEngine.getArts(audio);
                        if (arts != null) {
                            for (ArtSize artSize : ArtSize.values()) {
                                String artUrl = arts.getArtUrl(artSize);
                                if (artUrl == null) {
                                    return null;
                                }

                                audioDatabase.downloadAndSaveAudioArt(audio, artUrl, artSize);
                            }
                        }

                        return arts;
                    }

                    @Override
                    public void onComplete(ArtCollection artCollection, IOException error) {
                        if (error == null) {
                            audio.cloneArtUrlsFrom(artCollection);
                            updatedAudiosCount++;

                            if (updatedAudiosCount >= songsCount - 1) {
                                updatedAudiosCount = -1;
                                stopSelf();
                            }
                        } else {
                            error.printStackTrace();
                        }
                    }
                });
            }
        }
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
