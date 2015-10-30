package com.tiksem.media.search.updating;

import com.tiksem.media.data.ArtCollection;
import com.tiksem.media.data.ArtSize;
import com.tiksem.media.data.Audio;
import com.tiksem.media.local.FlyingDogAudioDatabase;
import com.tiksem.media.search.InternetSearchEngine;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;

/**
 * Created by stykhonenko on 30.10.15.
 */
public class ArtUtils {
    public interface OnUpdated {
        void onUpdated();
    }

    public static Threading.Task<IOException, ArtCollection> crateUpdateAudioArtsTask(
            final InternetSearchEngine internetSearchEngine,
            final FlyingDogAudioDatabase audioDatabase,
            final Audio audio,
            final OnUpdated onUpdated) {
        return new Threading.Task<IOException, ArtCollection>() {
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
                    onUpdated.onUpdated();
                } else {
                    error.printStackTrace();
                }
            }
        };
    }
}
