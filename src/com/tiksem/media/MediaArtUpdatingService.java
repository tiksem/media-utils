package com.tiksem.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import com.tiksem.media.data.Album;
import com.tiksem.media.data.ArtSize;
import com.tiksem.media.local.LocalAudioDataBase;
import com.tiksem.media.search.InternetSearchEngine;
import com.utils.framework.io.IOUtilities;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by CM on 8/31/2014.
 */
public class MediaArtUpdatingService {
    private final ThreadPoolExecutor threadPoolExecutor;
    private LocalAudioDataBase localAudioDataBase;
    private InternetSearchEngine internetSearchEngine;
    private volatile boolean isPaused = false;
    private OnAlbumArtUpdated onAlbumArtUpdated;
    private Handler handler = new Handler();

    public interface OnAlbumArtUpdated {
        void onAlbumArtUpdated(Album album);
    }

    public MediaArtUpdatingService(LocalAudioDataBase localAudioDataBase,
                                   InternetSearchEngine internetSearchEngine,
                                   OnAlbumArtUpdated onAlbumArtUpdated) {
        this.localAudioDataBase = localAudioDataBase;
        this.internetSearchEngine = internetSearchEngine;
        this.onAlbumArtUpdated = onAlbumArtUpdated;

        threadPoolExecutor =
                new ThreadPoolExecutor(1, 1, 50000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                        Threading.lowPriorityThreadFactory());
    }

    private void updateAlbumArtInternal(final Album album) {
        while (isPaused) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                return;
            }
        }

        if(album.getArtUrl(ArtSize.SMALL) != null && album.getArtUrl(ArtSize.MEDIUM) != null &&
                album.getArtUrl(ArtSize.LARGE) != null){
            return;
        }

        if (internetSearchEngine.fillAlbumArts(album)) {
            String largeArtPath = album.getArtUrl(ArtSize.LARGE);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        IOUtilities.getBufferedInputStreamFromUrl(largeArtPath));
                localAudioDataBase.setAlbumArt(bitmap, album.getId());
                if(onAlbumArtUpdated != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onAlbumArtUpdated.onAlbumArtUpdated(album);
                        }
                    });
                }

            } catch (IOException e) {
                Log.e(getClass().getName(), "Error loading art");
            }
        }
    }

    public void updateAlbumArt(final Album album) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                updateAlbumArtInternal(album);
            }
        });
    }

    public void updateAllAlbumsArt() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Album> albums = localAudioDataBase.getAlbums();
                for (Album album : albums) {
                    updateAlbumArtInternal(album);
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
