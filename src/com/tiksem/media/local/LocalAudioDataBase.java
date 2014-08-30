package com.tiksem.media.local;

import com.tiksem.media.data.Album;
import com.tiksem.media.data.Artist;
import com.tiksem.media.data.Audio;
import com.tiksem.media.data.PlayList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 02.01.13
 * Time: 3:04
 * To change this template use File | Settings | File Templates.
 */
public interface LocalAudioDataBase {
    public List<Audio> getSongs();
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

    public PlayList addPlayList(String name);
    public Artist addArtist(String name);
    public Album addAlbum(String albumName, String artistName);
    public boolean addSongToPlayList(Audio audio, PlayList playList);
    public List<PlayList> getPlayListsWhereSongCanBeAdded(Audio audio);

    public void commitAudioChangesToDataBase(Audio audio);

    public interface OnArtsUpdatingFinished{
        void onFinished();
    }

    public void startAlbumArtsUpdating(OnArtsUpdatingFinished onArtsUpdatingFinishedListener);
}
