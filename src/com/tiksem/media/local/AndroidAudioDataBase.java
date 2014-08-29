package com.tiksem.media.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.tiksem.media.data.Album;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.data.PlayList;
import com.utilsframework.android.string.Strings;

import java.util.concurrent.Executor;

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

    private void initAudiosFromCursor(Cursor cursor){
        int count = cursor.getCount();

        for(int i = 0; i < count; i++){
            final int artistNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            final int artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
            final int albumNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            final int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            final int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            final int urlColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            final String artistName = cursor.getString(artistNameColumn);
            final long artistId = cursor.getLong(artistIdColumn);
            final String albumName = cursor.getString(albumNameColumn);
            final long albumId = cursor.getLong(albumIdColumn);
            final String title = cursor.getString(titleColumn);
            final long id = cursor.getLong(idColumn);
            final String url = cursor.getString(urlColumn);

            Artist artist = getOrCreateArtistWithId(artistId);
            artist.setName(artistName);

            Album album = getOrCreateAlbumWithId(albumId);
            album.setName(albumName);
            album.setArtistName(artistName);

            Audio audio = getOrCreateAudioWithId(id);
            audio.setName(title);
            audio.setUrl(url);

            writeAudioAndAlbumUsingArtist(audio,album,artist);

            cursor.moveToNext();
        }
    }

    @Override
    protected void initAudios(){
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1"
                + " AND " + MediaStore.Audio.Media.DATA + "<> ''";

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
}
