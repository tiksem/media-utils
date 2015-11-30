package com.tiksem.media.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tiksem.media.data.*;
import com.utils.framework.io.IOUtilities;
import com.utils.framework.strings.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    private List<Audio> internetAudios = new ArrayList<>();

    private File getArtistArtFile(Artist artist, ArtSize artSize) {
        String filesDir = getFilesDir();
        String path = filesDir + "/arts/artist/" + artSize + "_" + artist.getName();
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
        initInternetPlayLists();
        initArts();
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

                internetAudios.add(audio);
            }
        } finally {
            cursor.close();
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
                if (audio != null) {
                    if (name != null) {
                        audio.setName(name);
                    }
                    if (artistName != null && !audio.getArtistName().equals(artistName)) {
                        replaceArtist(artistName, audio);
                        audio.setArtistName(artistName);
                    }
                } else {
                    dataBase.delete(AUDIO_TABLE, ID + "=" + id, null);
                }
            }
        } finally {
            cursor.close();
        }
    }

    private interface ArtFileProvider<T extends ArtCollection> {
        File getArtFile(T artCollection, ArtSize artSize);
    }

    private class ArtistArtFileProvider implements ArtFileProvider<Artist> {
        @Override
        public File getArtFile(Artist artCollection, ArtSize artSize) {
            return getArtistArtFile(artCollection, artSize);
        }
    }

    private class AlbumArtFileProvider implements ArtFileProvider<Album> {
        @Override
        public File getArtFile(Album artCollection, ArtSize artSize) {
            return getAlbumArtFile(artCollection, artSize);
        }
    }

    private class AudioArtFileProvider implements ArtFileProvider<Audio> {
        @Override
        public File getArtFile(Audio artCollection, ArtSize artSize) {
            return getAudioArtFile(artCollection, artSize);
        }
    }

    private <T extends ArtCollection> void initArts(List<T> artCollections, ArtFileProvider<T> artFileProvider) {
        for (T artCollection : artCollections) {
            for (ArtSize artSize : ArtSize.values()) {
                File artFile = artFileProvider.getArtFile(artCollection, artSize);
                if (artFile.exists()) {
                    artCollection.setArtUrl(artSize, "file://" + artFile.getAbsolutePath());
                }
            }
        }
    }

    private void initArts() {
        initArts(getSongs(), new AudioArtFileProvider());
        initArts(getAlbums(), new AlbumArtFileProvider());
        initArts(getArtists(), new ArtistArtFileProvider());
    }

    private void deleteAllArts(ArtCollection artCollection) {
        for (ArtSize size : ArtSize.values()) {
            String artUrl = artCollection.getArtUrl(size);
            if (artUrl != null) {
                new File(artUrl).delete();
            }
        }
    }

    private void replaceArtist(String newArtistName, Audio audio) {
        String oldArtistName = audio.getArtistName();
        Artist removedArtist = removeTrackFromArtist(oldArtistName, audio);
        if (removedArtist != null) {
            deleteAllArts(removedArtist);
        }

        Artist artist = addTrackToArtist(newArtistName, audio);
        if (artist != null) {
            for (ArtSize artSize : ArtSize.values()) {
                File artistArtPath = getArtistArtFile(artist, artSize);
                if (artistArtPath.exists()) {
                    artist.setArtUrl(artSize, "file://" + artistArtPath.getAbsolutePath());
                }
            }
        }
    }

    private void updateAudioInDataBase(Audio audio) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(NAME, audio.getName());
        contentValues.put(ID, audio.getId());
        contentValues.put(ARTIST_NAME, audio.getArtistName());
        dataBase.replace(AUDIO_TABLE, null, contentValues);
    }

    public void setArtistName(long audioId, String artistName) {
        Audio audio = getSongById(audioId);

        if (Strings.isEmpty(artistName)) {
            throw new IllegalArgumentException("ArtistName is null or empty");
        }

        if (audio.getArtistName().equals(artistName)) {
            return;
        }

        checkAudioLocal(audio);

        replaceArtist(artistName, audio);
        audio.setArtistName(artistName);
        updateAudioInDataBase(audio);
    }

    public void setAudioName(long audioId, String name) {
        Audio audio = getSongById(audioId);

        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Name is null or empty");
        }

        if (audio.getName().equals(name)) {
            return;
        }

        checkAudioLocal(audio);

        audio.setName(name);
        updateAudioInDataBase(audio);
    }

    private void checkAudioLocal(Audio audio) {
        if (!audio.isLocal()) {
            throw new IllegalArgumentException("Audio is not local");
        }
    }

    private long addInternetAudio(Audio audio) {
        if (audio == null) {
            throw new NullPointerException();
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
        String path = getArtistArtFile(artist, artSize).getAbsolutePath();
        saveArt(artist, artUrl, artSize, path);
    }

    public void downloadAndSaveAlbumArt(Album album, String artUrl, ArtSize artSize) throws IOException {
        String path = getAlbumArtFile(album, artSize).getAbsolutePath();
        saveArt(album, artUrl, artSize, path);
    }

    public List<Audio> getInternetAudios() {
        return internetAudios;
    }

    @Override
    protected void removeAudioFromPlayListInDatabase(PlayList playList, Audio audio) {
        if (audio.isLocal()) {
            super.removeAudioFromPlayListInDatabase(playList, audio);
        } else {
            if (getPlayListsWhereSongCanBeAdded(audio).size() == getPlayLists().size()) {
                internetAudios.remove(audio);
                String where = ID + "=" + audio.getId();
                dataBase.delete(INTERNET_AUDIO_TABLE_NAME, where, null);
            }

            String where = ID + "=" + playList.getId() + " AND " + AUDIO_ID + "=" + audio.getId();
            dataBase.delete(INTERNET_PLAYLISTS_TABLE_NAME, where, null);
        }
    }
}
