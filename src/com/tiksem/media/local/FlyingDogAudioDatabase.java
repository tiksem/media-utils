package com.tiksem.media.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tiksem.media.data.ArtCollection;
import com.tiksem.media.data.ArtSize;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;

import java.io.File;

/**
 * Created by stykhonenko on 29.10.15.
 */
public class FlyingDogAudioDatabase extends AndroidAudioDataBase {
    private static final String AUDIO_TABLE = "Audio";

    private static final String ARTIST_NAME = "artistName";
    private static final String NAME = "name";
    private static final String ID = "id";
    static final String CREATE_AUDIO_TABLE = "CREATE TABLE " + AUDIO_TABLE + " " +
            "(" +
            ID + " BIGINT PRIMARY KEY," +
            NAME + " TEXT, " +
            ARTIST_NAME + " TEXT" +
            ")";
    private final SQLiteDatabase dataBase;
    private Context context;

    private String getArtistArtPath(String artistName) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File filesDir = contextWrapper.getFilesDir();
        return filesDir.getAbsolutePath() + "/arts/artist/" + artistName;
    }

    public FlyingDogAudioDatabase(Context context) {
        super(context.getContentResolver());
        dataBase = FlyingDogDatabaseHelper.getInstance(context).getWritableDatabase();
        this.context = context;

        Cursor cursor = dataBase.query(AUDIO_TABLE, null, null, null, null, null, null);
        try {
            int idColumnIndex = cursor.getColumnIndexOrThrow(ID);
            int nameColumnIndex = cursor.getColumnIndexOrThrow(NAME);
            int artistNameColumnIndex = cursor.getColumnIndexOrThrow(ARTIST_NAME);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String artistName = cursor.getString(artistNameColumnIndex);

                Audio audio = getAudioById(id);
                audio.setName(name);
                if (artistName != null && !audio.getArtistName().equals(artistName)) {
                    audio.setArtistName(artistName);
                    replaceArtist(artistName, audio);
                }
            }
        } finally {
            cursor.close();
        }
    }

    private void deleteAllArts(ArtCollection artCollection) {
        for (ArtSize size : ArtSize.values()) {
            new File(artCollection.getArtUrl(size)).delete();
        }
    }

    private void replaceArtist(String artistName, Audio audio) {
        Artist removedArtist = removeTrackFromArtist(artistName, audio);
        if (removedArtist != null) {
            deleteAllArts(removedArtist);
        }

        Artist artist = addTrackToArtist(artistName, audio);
        if (artist != null) {
            String artistArtPath = getArtistArtPath(artistName);
            if (new File(artistArtPath).exists()) {
                artist.setUrlForAllArts(artistArtPath);
            }
        }
    }

    public void setArtistName(Audio audio, String artistName) {
        if (artistName == null) {
            throw new NullPointerException();
        }

        if (audio.getArtistName().equals(artistName)) {
            return;
        }

        audio.setArtistName(artistName);
        replaceArtist(artistName, audio);
    }

    public void setAudioName(Audio audio, String name) {
        if (name == null) {
            throw new NullPointerException();
        }

        if (audio.getName().equals(name)) {
            return;
        }

        audio.setName(name);
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(ID, audio.getId());
        contentValues.put(NAME, name);
        dataBase.replace(AUDIO_TABLE, null, contentValues);
    }
}
