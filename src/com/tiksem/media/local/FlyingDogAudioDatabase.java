package com.tiksem.media.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tiksem.media.data.*;
import com.utils.framework.io.IOUtilities;

import java.io.File;
import java.io.IOException;

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

    private static final String MEDIUM_ART = "mediumArt";
    private static final String SMALL_ART = "smallArt";
    private static final String BIG_ART = "bigArt";
    private static final String INTERNET_AUDIO_TABLE_NAME = "InternetAudio";
    static final String CREATE_INTERNET_AUDIO_TABLE = "CREATE TABLE " + INTERNET_AUDIO_TABLE_NAME + " " +
            "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            NAME + " TEXT," +
            ARTIST_NAME + " TEXT," +
            SMALL_ART + " TEXT," +
            MEDIUM_ART + " TEXT," +
            BIG_ART + " TEXT" +
            ")";

    private static final String AUDIO_ID = "audioId";
    private static final String INTERNET_PLAYLISTS_TABLE_NAME = "InternetPlaylists";
    static final String CREATE_INTERNET_PLAYLISTS_DATABASE = "CREATE TABLE " + INTERNET_PLAYLISTS_TABLE_NAME + " " +
            "(" +
            ID + " INTEGER," +
            AUDIO_ID + " INTEGER" +
            ")";

    private final SQLiteDatabase dataBase;
    private Context context;

    private File getArtistArtFile(String artistName, ArtSize artSize) {
        String filesDir = getFilesDir();
        String path = filesDir + "/arts/artist/" + artSize + "_" + artistName;
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }

    private File getAudioArtFile(Audio audio, ArtSize artSize) {
        String filesDir = getFilesDir();
        String path = filesDir + "/arts/audio/" + audio.getId() + artSize;
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }

    private File getAlbumArtFile(Album album, ArtSize artSize) {
        String filesDir = getFilesDir();
        String path = filesDir + "/arts/album/" + album.getId() + artSize;
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }

    private String getFilesDir() {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        return contextWrapper.getFilesDir().getAbsolutePath();
    }

    public FlyingDogAudioDatabase(Context context) {
        super(context.getContentResolver());
        dataBase = FlyingDogDatabaseHelper.getInstance(context).getWritableDatabase();
        this.context = context;

        initLocalAudios();
        initAudioArts();
        initInternetPlayLists();
    }

    private void initInternetPlayLists() {
        Cursor cursor = dataBase.query(INTERNET_AUDIO_TABLE_NAME, null, null, null, null, null, null);
        try {
            int idColumnIndex = cursor.getColumnIndexOrThrow(ID);
            int nameColumnIndex = cursor.getColumnIndexOrThrow(NAME);
            int artistNameColumnIndex = cursor.getColumnIndexOrThrow(ARTIST_NAME);
            int smallArtColumnIndex = cursor.getColumnIndexOrThrow(SMALL_ART);
            int mediumArtColumnIndex = cursor.getColumnIndexOrThrow(MEDIUM_ART);
            int bigArtColumnIndex = cursor.getColumnIndexOrThrow(BIG_ART);

            while (cursor.moveToNext()) {
                Audio audio = Audio.createInternetAudio(cursor.getInt(idColumnIndex));
                audio.setName(cursor.getString(nameColumnIndex));
                audio.setArtistName(cursor.getString(artistNameColumnIndex));
                audio.setArtUrl(ArtSize.SMALL, cursor.getString(smallArtColumnIndex));
                audio.setArtUrl(ArtSize.MEDIUM, cursor.getString(mediumArtColumnIndex));
                audio.setArtUrl(ArtSize.LARGE, cursor.getString(bigArtColumnIndex));

                String sql = "SELECT " + ID + " FROM " + INTERNET_PLAYLISTS_TABLE_NAME +
                        " WHERE " + AUDIO_ID + "=" + audio.getId();

                Cursor playListsCursor = dataBase.rawQuery(sql, null);
                try {
                    int playListIdColumnIndex = playListsCursor.getColumnIndex(ID);

                    while (playListsCursor.moveToNext()) {
                        long playListId = playListsCursor.getLong(playListIdColumnIndex);
                        addTrackToPlayList(playListId, audio);
                    }
                } finally {
                    playListsCursor.close();
                }
            }
        } finally {
            cursor.close();
        }
    }

    private void initAudioArts() {
        for (Audio audio : getSongs()) {
            for (ArtSize artSize : ArtSize.values()) {
                File art = getAudioArtFile(audio, artSize);
                if (art.exists()) {
                    audio.setArtUrl(artSize, "file://" + art.getAbsolutePath());
                }
            }
        }
    }

    private void initLocalAudios() {
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
            for (ArtSize artSize : ArtSize.values()) {
                File artistArtPath = getArtistArtFile(artistName, artSize);
                if (artistArtPath.exists()) {
                    artist.setArtUrl(artSize, artistArtPath.getAbsolutePath());
                }
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

    private long addInternetAudio(Audio audio) {
        if (audio == null) {
            throw new NullPointerException();
        }

        if (audio.getUrl() == null) {
            throw new IllegalArgumentException("Invalid audio url");
        }

        ContentValues contentValues = new ContentValues(5);
        contentValues.put(NAME, audio.getName());
        contentValues.put(ARTIST_NAME, audio.getArtistName());
        contentValues.put(SMALL_ART, audio.getArtUrl(ArtSize.SMALL));
        contentValues.put(MEDIUM_ART, audio.getArtUrl(ArtSize.MEDIUM));
        contentValues.put(BIG_ART, audio.getArtUrl(ArtSize.LARGE));
        return dataBase.replace(INTERNET_AUDIO_TABLE_NAME, null, contentValues);
    }

    private void addAudioToInternetPlayList(long playListId, long audioId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, playListId);
        contentValues.put(AUDIO_ID, audioId);
        dataBase.insert(INTERNET_PLAYLISTS_TABLE_NAME, null, contentValues);
    }

    @Override
    protected void addAudioToPlayListInDatabase(PlayList playList, Audio audio) {
        if (audio.isLocal()) {
            super.addAudioToPlayListInDatabase(playList, audio);
        } else {
            long id = addInternetAudio(audio);
            audio.setId(id);
            addAudioToInternetPlayList(playList.getId(), id);
        }
    }

    private void saveArt(ArtCollection artCollection, String artUrl,
                         ArtSize artSize, String path)
            throws IOException {
        IOUtilities.downloadFile(artUrl, path);
        artCollection.setArtUrl(artSize, "file://" + path);
    }

    public void downloadAndSaveAudioArt(Audio audio, String artUrl, ArtSize artSize) throws IOException {
        String path = getAudioArtFile(audio, artSize).getAbsolutePath();
        saveArt(audio, artUrl, artSize, path);
    }

    public void downloadAndSaveArtistArt(Artist artist, String artUrl, ArtSize artSize) throws IOException {
        String path = getArtistArtFile(artist.getName(), artSize).getAbsolutePath();
        saveArt(artist, artUrl, artSize, path);
    }

    public void downloadAndSaveAlbumArt(Album album, String artUrl, ArtSize artSize) throws IOException {
        String path = getAlbumArtFile(album, artSize).getAbsolutePath();
        saveArt(album, artUrl, artSize, path);
    }
}
