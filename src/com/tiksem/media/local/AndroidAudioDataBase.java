package com.tiksem.media.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.tiksem.media.data.*;
import com.utils.framework.strings.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

        Artist artist = getOrCreateArtistWithName(artistName);
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
            return null;
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
    protected void addAudioToPlayListInDatabase(PlayList playList, Audio audio) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playList.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio.getId());
        contentResolver.insert(uri, contentValues);
    }

    @Override
    protected void setAlbumIdToAudioInDataBase(Long albumId, long audioId) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Audio.AudioColumns._ID + "=" + audioId;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.ALBUM_ID, albumId);
        contentResolver.update(uri, contentValues, where, null);
    }

    @Override
    protected void removeAlbumFromDataBase(long id) {
        throw new UnsupportedOperationException();
    }
}
