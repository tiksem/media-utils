package com.tiksem.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import com.tiksem.media.data.*;
import com.tiksem.media.local.LocalAudioDataBase;
import com.tiksem.media.search.InternetSearchEngine;
import com.utils.framework.io.IOUtilities;
import com.utilsframework.android.threading.HighPriorityRunnable;
import com.utilsframework.android.threading.OnComplete;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by CM on 8/31/2014.
 */
public class MediaUpdatingService {
    private final ThreadPoolExecutor threadPoolExecutor;
    private LocalAudioDataBase localAudioDataBase;
    private InternetSearchEngine internetSearchEngine;
    private volatile boolean isPaused = false;
    private Handler handler = new Handler();

    public interface OnAlbumArtUpdated {
        void onAlbumArtUpdated(Album album);
    }

    public interface OnArtistArtUpdated {
        void onArtistArtUpdated(Artist artist);
    }

    public interface OnAlbumOfAudioUpdated {
        void onUpdateFinished(Album album, Audio audio);
    }

    public interface OnAlbumUpdateFinished {
        void onFinish(boolean success);
    }

    public MediaUpdatingService(LocalAudioDataBase localAudioDataBase,
                                InternetSearchEngine internetSearchEngine) {
        this.localAudioDataBase = localAudioDataBase;
        this.internetSearchEngine = internetSearchEngine;

        threadPoolExecutor =
                new ThreadPoolExecutor(0, 1, 50000, TimeUnit.MILLISECONDS, Threading.highPriorityBlockingQueue(),
                        Threading.lowPriorityThreadFactory());
    }

    private void blockIfPaused() {
        while (isPaused) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void updateAlbumOfAudioInternal(Audio audio, final OnAlbumUpdateFinished onComplete) {
        blockIfPaused();

        final boolean success = internetSearchEngine.tryFillAlbum(audio);
        if(success){
            localAudioDataBase.commitAudioChangesToDataBase(audio);
            updateArtInternal(localAudioDataBase.getAlbumById(audio.getAlbumId()), null);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onComplete != null) {
                    onComplete.onFinish(success);
                }
            }
        });
    }

    public void updateAlbumOfAudio(final Audio audio, final OnAlbumUpdateFinished onFinish) {
        threadPoolExecutor.execute(new HighPriorityRunnable() {
            @Override
            public void run() {
                updateAlbumOfAudioInternal(audio, onFinish);
            }
        });
    }

    public void updateAlbumsOfAudios(final OnAlbumOfAudioUpdated onAlbumOfAudioUpdated) {
        List<Audio> audios = localAudioDataBase.getSongs();
        for(final Audio audio : audios){
            if (audio.getAlbumId() <= 0) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        updateAlbumOfAudioInternal(audio, new OnAlbumUpdateFinished() {
                            @Override
                            public void onFinish(boolean success) {
                                if(success && onAlbumOfAudioUpdated != null){
                                    onAlbumOfAudioUpdated.onUpdateFinished(
                                            localAudioDataBase.getAlbumById(audio.getAlbumId()), audio);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void updateArtInternal(final ArtCollection artCollection, final OnComplete onComplete) {
        blockIfPaused();

        if(artCollection.getArtUrl(ArtSize.SMALL) != null && artCollection.getArtUrl(ArtSize.MEDIUM) != null &&
                artCollection.getArtUrl(ArtSize.LARGE) != null){
            if(localAudioDataBase.hasTrueArt(artCollection)){
                return;
            }
        }

        if (internetSearchEngine.tryFillAlbumArts(artCollection)) {
            String largeArtPath = artCollection.getArtUrl(ArtSize.LARGE);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        IOUtilities.getBufferedInputStreamFromUrl(largeArtPath));
                if (bitmap != null) {
                    localAudioDataBase.setArt(bitmap, artCollection);
                }

                if(onComplete != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onComplete.onFinish();
                        }
                    });
                }

            } catch (IOException e) {
                Log.e(getClass().getName(), "Error loading art");
            }
        }
    }

    public void updateAlbumArt(final Album album, final OnAlbumArtUpdated onAlbumArtUpdated) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                updateArtInternal(album, new OnComplete() {
                    @Override
                    public void onFinish() {
                        if (onAlbumArtUpdated != null) {
                            onAlbumArtUpdated.onAlbumArtUpdated(album);
                        }
                    }
                });
            }
        });
    }

    public void updateAllAlbumsArt(final OnAlbumArtUpdated onAlbumArtUpdated) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Album> albums = localAudioDataBase.getAlbums();
                for (final Album album : albums) {
                    updateArtInternal(album, new OnComplete() {
                        @Override
                        public void onFinish() {
                            if(onAlbumArtUpdated != null){
                                onAlbumArtUpdated.onAlbumArtUpdated(album);
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateArtistArt(final Artist artist, final OnArtistArtUpdated onArtistArtUpdated) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                updateArtInternal(artist, new OnComplete() {
                    @Override
                    public void onFinish() {
                        if (onArtistArtUpdated != null) {
                            onArtistArtUpdated.onArtistArtUpdated(artist);
                        }
                    }
                });
            }
        });
    }

    public void updateAllArtistsArt(final OnArtistArtUpdated onArtistArtUpdated) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Artist> artists = localAudioDataBase.getArtists();
                for (final Artist artist : artists) {
                    updateArtInternal(artist, new OnComplete() {
                        @Override
                        public void onFinish() {
                            if(onArtistArtUpdated != null){
                                onArtistArtUpdated.onArtistArtUpdated(artist);
                            }
                        }
                    });
                }
            }
        });
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }
}
