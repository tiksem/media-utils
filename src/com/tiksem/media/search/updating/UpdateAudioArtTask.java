package com.tiksem.media.search.updating;

import com.tiksem.media.data.ArtCollection;
import com.tiksem.media.data.ArtSize;
import com.tiksem.media.data.Audio;
import com.tiksem.media.local.FlyingDogAudioDatabase;
import com.tiksem.media.search.InternetSearchEngine;
import com.utilsframework.android.threading.Threading;

import java.io.IOException;

/**
 * Created by stykhonenko on 02.11.15.
 */
public abstract class UpdateAudioArtTask extends Threading.Task<IOException, ArtCollection> {
    private InternetSearchEngine internetSearchEngine;
    private FlyingDogAudioDatabase audioDatabase;
    private Audio audio;

    public UpdateAudioArtTask(InternetSearchEngine internetSearchEngine,
                              FlyingDogAudioDatabase audioDatabase, Audio audio) {
        this.internetSearchEngine = internetSearchEngine;
        this.audioDatabase = audioDatabase;
        this.audio = audio;
    }

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
            onUpdated();
        } else {
            error.printStackTrace();
        }
    }

    protected abstract void onUpdated();
}
