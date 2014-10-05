package com.tiksem.media.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.tiksem.media.data.*;
import com.utils.framework.io.IOUtilities;
import com.utils.framework.strings.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 27.10.12
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class AndroidAudioDataBase extends MappedLocalAudioDataBase{
    private ContentResolver contentResolver;

    public AndroidAudioDataBase(ContentResolver contentResolver) {
        super();
        this.contentResolver = contentResolver;
        executeInitAudiosIfNeed();
        executeInitPlayListsIfNeed();
    }

    private static class Media {
        Audio audio;
        Artist artist;
        Album album;

        private Media(Audio audio, Artist artist, Album album) {
            this.audio = audio;
            this.artist = artist;
            this.album = album;
        }
    }

    private Media getAudioFromCursor(Cursor cursor, boolean useCachedAudio) {
        final int artistNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        final int artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
        final int albumNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        final int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
        final int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        final int urlColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

        final String artistName = cursor.getString(artistNameColumn);
        final long artistId = cursor.getLong(artistIdColumn);
        String albumName = cursor.getString(albumNameColumn);
        long albumId = cursor.getLong(albumIdColumn);
        final String title = cursor.getString(titleColumn);
        final long id = cursor.getLong(idColumn);
        final String url = cursor.getString(urlColumn);

        if(TextUtils.isEmpty(albumName)){
            albumName = null;
            albumId = -1;
        }

        Artist artist = getOrCreateArtistWithId(artistId);
        artist.setName(artistName);

        Album album = null;
        if (albumId > 0) {
            album = getOrCreateAlbumWithId(albumId);
            album.setName(albumName);
            album.setArtistName(artistName);
        }

        Audio audio = useCachedAudio ? getOrCreateAudioWithId(id) : Audio.createLocalAudio(id);
        audio.setName(title);
        audio.setArtistName(artistName);
        audio.setUrl(url);
        audio.setArtistId(artistId);

        return new Media(audio, artist, album);
    }

    private void initAudiosFromCursor(Cursor cursor){
        int count = cursor.getCount();

        for(int i = 0; i < count; i++){
            Media media = getAudioFromCursor(cursor, true);
            writeAudioAndAlbumUsingArtist(media.audio, media.album, media.artist);
            cursor.moveToNext();
        }
    }

    private static final String DEFAULT_AUDIO_QUERY_WHERE = MediaStore.Audio.Media.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.Media.DATA + "<> ''";

    private Cursor queryAudios(String where) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final String[] audio_cursor_cols = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA,
        };

        final Cursor cursor = contentResolver.query(uri, audio_cursor_cols, where, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    private Audio getAudioByIdFromDataBase(long id) {
        String where = DEFAULT_AUDIO_QUERY_WHERE + " AND " + MediaStore.Audio.Media._ID + " = " + id;
        Cursor cursor = queryAudios(where);
        if(cursor.getCount() <= 0){
            return null;
        }

        return getAudioFromCursor(cursor, false).audio;
    }

    @Override
    protected void initAudios(){
        Cursor cursor = queryAudios(DEFAULT_AUDIO_QUERY_WHERE);
        initAudiosFromCursor(cursor);
    }

    private PlayList getPlayListByIdFromDataBase(long id){
        String[] proj = {
            MediaStore.Audio.Playlists.Members.AUDIO_ID,
        };

        final String where = MediaStore.Audio.Playlists.Members.IS_MUSIC + "=1"
                + " AND " + MediaStore.Audio.Playlists.Members.DATA + "<> ''";

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",id);
        Cursor cursor = contentResolver.query(uri,proj,where,null,null);
        cursor.moveToFirst();

        int count = cursor.getCount();
        PlayList playList = getOrCreatePlayListWithId(id);

        for(int i = 0; i<count; i++){
            final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            final long audioId = cursor.getLong(idColumn);

            Audio audio = getOrCreateAudioWithId(audioId);
            addTrackToPlayList(id, audio);

            cursor.moveToNext();
        }

        return playList;
    }

    @Override
    protected void initPlayLists(){
        final Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        final String[] playlist_cursor_cols = {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };

        final Cursor cursor = contentResolver.query(uri, playlist_cursor_cols, null, null, null);
        cursor.moveToFirst();

        int count = cursor.getCount();

        for(int i = 0; i<count; i++){
            final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
            final int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);

            final String name = cursor.getString(nameColumn);
            final long id = cursor.getLong(idColumn);

            PlayList playList = getPlayListByIdFromDataBase(id);
            playList.setName(name);

            cursor.moveToNext();
        }
    }

    @Override
    protected String getArtistArtUrl(long artistId) {
        File file = new File(generateArtistArtPath(artistId));
        if(file.exists()){
            return "file://" + file.getAbsolutePath();
        } else {
            return null;
        }
    }

    @Override
    protected String getAlbumArtUrl(long albumId){
        if(albumId <= 0){
            return null;
        }

        String[] columns = new String[]{
                MediaStore.Audio.Albums.ALBUM_ART
        };

        String where = MediaStore.Audio.Albums._ID + " = " + albumId;
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                columns, where, null, null);

        cursor.moveToFirst();
        int artColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
        String url = cursor.getString(artColumn);

        if(url == null){
            File file = new File(generateAlbumArtPath(albumId));
            if(file.exists()){
                return "file://" + file.getAbsolutePath();
            } else {
                return null;
            }
        } else {
            return "file://" + url;
        }
    }

    @Override
    protected void addPlayListToDatabase(PlayList playList) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Playlists.NAME, playList.getName());
        uri = contentResolver.insert(uri, contentValues);
        long id = Strings.getLongFromString(uri.toString());
        playList.setId(id);
    }

    @Override
    protected void addArtistToDatabase(Artist artist) {
        long id = Collections.max(getArtists(), new Comparator<Artist>() {
            @Override
            public int compare(Artist a, Artist b) {
                return (int) (a.getId() - b.getId());
            }
        }).getId() + 1;
        artist.setId(id);
    }

    @Override
    protected void addAlbumToDatabase(Album album) {
        long id = Collections.max(getAlbums(), new Comparator<Album>() {
            @Override
            public int compare(Album a, Album b) {
                return (int) (a.getId() - b.getId());
            }
        }).getId() + 1;
        album.setId(id);
    }

    @Override
    protected void addAudioToPlayListInDatabase(PlayList playList, Audio audio) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playList.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio.getId());
        contentResolver.insert(uri, contentValues);
    }

    @Override
    public void commitAudioChangesToDataBase(Audio audio) {
        Long id = audio.getId();
        if(getAudioById(id) == null){
            throw new IllegalArgumentException("Audio is not in database");
        }

        Audio dataBaseAudio = getAudioByIdFromDataBase(id);

        if(dataBaseAudio == null){
            throw new RuntimeException("Broken database!");
        }

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Audio.AudioColumns._ID + "=" + id;

        ContentValues contentValues = new ContentValues();

        String name = audio.getName();
        if(!dataBaseAudio.getName().equals(name)){
            contentValues.put(MediaStore.Audio.Media.TITLE, name);
        }

        String artistName = audio.getArtistName();
        Artist artist = getArtistByName(artistName);
        if(artist == null){
            throw new IllegalArgumentException("Artist is not registered in database");
        }

        long artistId = artist.getId();

        if(!dataBaseAudio.getArtistName().equals(artistName)){

            long prevArtistId = dataBaseAudio.getArtistId();
            List<Audio> prevArtistAudios = getSongsByArtistId(prevArtistId);
            prevArtistAudios.remove(audio);
            if(prevArtistAudios.isEmpty()){
                removeArtistWithId(prevArtistId);
            }

            audio.setArtistId(artistId);
            contentValues.put(MediaStore.Audio.Media.ARTIST_ID, artistId);
            contentValues.put(MediaStore.Audio.Media.ARTIST, artistName);
            getSongsByArtistId(artistId).add(audio);
        }

        String albumName = audio.getAlbumName();
        if(albumName != null){

            Album album = getAlbumByNameAndArtistId(albumName, artistId);
            if(album == null){
                if(audio.getAlbumId() < 0){
                    album = addAlbum(albumName, artistName);
                } else {
                    throw new IllegalArgumentException("Album is not associated with the artist");
                }
            }

            audio.setAlbumId(album.getId());

            contentValues.put(MediaStore.Audio.Media.ALBUM_ID, album.getId());
            contentValues.put(MediaStore.Audio.Media.ALBUM, albumName);

            if(album.getArtUrl(ArtSize.LARGE) == null){
                String audioArtUrl = audio.getArtUrl(ArtSize.LARGE);
                if(audioArtUrl != null){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                IOUtilities.getBufferedInputStreamFromUrl(audioArtUrl));
                        setArt(bitmap, album);
                        audio.cloneArtUrlsFrom(album);
                    } catch (IOException e) {

                    }
                }
            }
        }

        if (contentValues.size() > 0) {
            contentResolver.update(uri, contentValues, where, null);
        }
    }

    @Override
    protected void removeAlbumFromDataBase(long id) {
        new File(generateAlbumArtPath(id)).delete();
    }

    @Override
    protected void setAlbumIdToAudioInDataBase(Long albumId, long audioId) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Audio.AudioColumns._ID + "=" + audioId;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.ALBUM_ID, albumId);
        contentResolver.update(uri, contentValues, where, null);
    }

    private String generateAlbumArtPath(long albumId) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                AndroidAudioDataBase.class.getName() + "_arts/"
                + "art" + albumId;
    }

    private String generateArtistArtPath(long artistId) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                AndroidAudioDataBase.class.getName() + "_arts/"
                + "artist" + artistId;
    }

    private String saveArtToDatabase(String path, Bitmap bitmap) {
        File file = new File(path);
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (IOException e) {
            return null;
        }

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            return null;
        }

        return "file://" + path;
    }

    @Override
    protected String saveAlbumArtToDataBase(Bitmap bitmap, long albumId) {
        String path = generateAlbumArtPath(albumId);
        return saveArtToDatabase(path, bitmap);
    }

    @Override
    protected String saveArtistArtToDataBase(Bitmap bitmap, long artistId) {
        String path = generateArtistArtPath(artistId);
        return saveArtToDatabase(path, bitmap);
    }
}
