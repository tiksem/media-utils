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
    public List<Audio> getSongs();

    Artist getArtistById(long id);

    public List<PlayList> getPlayLists();
    public List<Album> getAlbums();
    public List<Artist> getArtists();

    public List<Audio> getSongsOfArtist(Artist artist);
    public List<Audio> getSongsOfAlbum(Album album);
    public List<Audio> getSongsOfPlayList(PlayList playList);
    public List<Audio> getSongsWithoutAlbumOfArtist(Artist artist);
    public List<Album> getAlbumsOfArtist(Artist artist);

    public boolean artistHasTracks(Artist artist);
    public boolean artistHasAlbums(Artist artist);

    public Audio getSongById(long id);
    public Album getAlbumById(long id);

    public PlayList addPlayList(String name);
    public Artist addArtist(String name);
    public Album addAlbum(String albumName, String artistName);
    public boolean addSongToPlayList(Audio audio, PlayList playList);
    public List<PlayList> getPlayListsWhereSongCanBeAdded(Audio audio);

    public void commitAudioChangesToDataBase(Audio audio);

    public interface OnArtsUpdatingFinished{
        void onFinished();
    }

    public AsyncTask startAlbumArtsUpdating(OnArtsUpdatingFinished onArtsUpdatingFinishedListener);
    public void setAlbumArt(Bitmap bitmap, long albumId);
    public void setArtistArt(Bitmap bitmap, long artistId);
    public void setArt(Bitmap bitmap, ArtCollection artCollection);
    public boolean hasTrueArt(ArtCollection artCollection);
}
