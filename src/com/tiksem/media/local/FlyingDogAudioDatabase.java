package com.tiksem.media.local;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.tiksem.media.data.Album;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.data.PlayList;

import java.io.File;
import java.util.List;

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
            ID + " BIGINT UNIQUE," +
            NAME + " TEXT" +
            ARTIST_NAME + " TEXT" +
            ")";
    private final SQLiteDatabase dataBase;
    private Context context;

    private String getArtistAlbumArtPath(String artistName) {
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
            int idColumnIndex = cursor.getColumnIndex(ID);
            int nameColumnIndex = cursor.getColumnIndex(NAME);
            int artistNameColumnIndex = cursor.getColumnIndex(ARTIST_NAME);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String artistName = cursor.getString(artistNameColumnIndex);
                removeTrackFromArtist(artistName, getAudioById(id));

                Audio audio = getAudioById(id);
                audio.setName(name);
                if (artistName != null && !audio.getArtistName().equals(artistName)) {
                    audio.setArtistName(artistName);
                    Artist artist = addTrackToArtist(artistName, audio);
                    artist.setUrlForAllArts(getArtistAlbumArtPath(artist.getName()));
                }
            }
        } finally {
            cursor.close();
        }
    }
}
