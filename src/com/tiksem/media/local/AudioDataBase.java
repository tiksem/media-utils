package com.tiksem.media.local;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.tiksem.media.data.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 02.01.13
 * Time: 3:04
 * To change this template use File | Settings | File Templates.
 */
public interface AudioDataBase {
    List<Audio> getSongs();

    Artist getArtistById(long id);

    List<PlayList> getPlayLists();
    List<Album> getAlbums();
    List<Artist> getArtists();

    List<Audio> getSongsOfArtist(Artist artist);
    List<Audio> getSongsOfAlbum(Album album);
    List<Audio> getSongsOfPlayList(PlayList playList);
    List<Audio> getSongsWithoutAlbumOfArtist(Artist artist);
    List<Album> getAlbumsOfArtist(Artist artist);

    boolean artistHasTracks(Artist artist);
    boolean artistHasAlbums(Artist artist);

    Audio getSongById(long id);
    Album getAlbumById(long id);

    PlayList addPlayList(String name);
    boolean addSongToPlayList(Audio audio, PlayList playList);
    List<PlayList> getPlayListsWhereSongCanBeAdded(Audio audio);

    interface OnArtsUpdatingFinished{
        void onFinished();
    }

    AsyncTask startAlbumArtsUpdating(OnArtsUpdatingFinished onArtsUpdatingFinishedListener);
}
