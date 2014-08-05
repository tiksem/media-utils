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
    public abstract List<Audio> getSongs();
    public abstract List<PlayList> getPlayLists();
    public abstract List<Album> getAlbums();
    public abstract List<Artist> getArtists();

    public abstract List<Audio> getSongsOfArtist(Artist artist);
    public abstract List<Audio> getSongsOfAlbum(Album album);
    public abstract List<Audio> getSongsOfPlayList(PlayList playList);
    public abstract List<Audio> getSongsWithoutAlbumOfArtist(Artist artist);
    public abstract List<Album> getAlbumsOfArtist(Artist artist);

    public abstract boolean artistHasTracks(Artist artist);
    public abstract boolean artistHasAlbums(Artist artist);

    public abstract Audio getSongById(int id);

    public interface OnArtsUpdatingFinished{
        void onFinished();
    }

    public abstract void startAlbumArtsUpdating(OnArtsUpdatingFinished onArtsUpdatingFinishedListener);
}
