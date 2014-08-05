package com.tiksem.media.local;

import android.os.AsyncTask;
import com.tiksem.media.data.*;
import com.utilsframework.android.threading.OnComplete;
import com.utilsframework.android.threading.Threading;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 09.02.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class MappedLocalAudioDataBase implements LocalAudioDataBase{
    private Map<Long,Audio> songsById;
    private Map<Long,Artist> artistsById = new LinkedHashMap<Long, Artist>();
    private Map<Long,Album> albumsById = new LinkedHashMap<Long, Album>();
    private Map<Long,PlayList> playListsById;

    private Map<Long,List<Audio>> songsByAlbumId =
            new LinkedHashMap<Long, List<Audio>>();
    private Map<Long,List<Audio>> songsByArtistId =
            new LinkedHashMap<Long, List<Audio>>();
    private Map<Long,List<Audio>> songsWithoutAlbumByArtistId =
            new LinkedHashMap<Long, List<Audio>>();
    private Map<Long,List<Audio>> songsByPlayListId =
            new LinkedHashMap<Long, List<Audio>>();

    private Map<Long,List<Album>> albumsByArtistId =
            new LinkedHashMap<Long, List<Album>>();

    private Map<Long,Long> albumIdArtistId = new LinkedHashMap<Long, Long>();

    protected MappedLocalAudioDataBase() {

    }

    public Audio getAudioById(long id){
        return songsById.get(id);
    }

    protected Audio getOrCreateAudioWithId(long id){
        Audio audio = getAudioById(id);
        if(audio == null){
            audio = Audio.createLocalAudio((int)id);
            songsById.put(id,audio);
        }

        return audio;
    }

    public Artist getArtistById(long id){
        return artistsById.get(id);
    }

    protected Artist getOrCreateArtistWithId(long id){
        Artist artist = getArtistById(id);
        if(artist == null){
            artist = Artist.createLocalArtist((int)id);
            artistsById.put(id,artist);
        }

        return artist;
    }

    public Album getAlbumById(long id){
        return albumsById.get(id);
    }

    protected Album getOrCreateAlbumWithId(long id){
        Album album = getAlbumById(id);
        if(album == null){
            album = Album.createLocalAlbum((int)id);
            albumsById.put(id, album);
        }

        return album;
    }

    public PlayList getPlayListById(long id){
        return playListsById.get(id);
    }

    protected PlayList getOrCreatePlayListWithId(long id){
        PlayList playList = getPlayListById(id);
        if(playList == null){
            playList = PlayList.createLocalPlayList((int)id);
            playListsById.put(id,playList);
        }

        return playList;
    }

    private void addTrackToMap(Map<Long,List<Audio>> map, long id, Audio track){
        List<Audio> audiosById = map.get(id);

        if(audiosById == null){
            audiosById = new ArrayList<Audio>();
            map.put(id,audiosById);
        }

        audiosById.add(track);
    }

    protected void addTrackToPlayList(long playListId, Audio audio){
        addTrackToMap(songsByPlayListId, playListId, audio);
    }

    protected void addTrackToAlbum(long albumId, Audio audio){
        addTrackToMap(songsByAlbumId, albumId, audio);
    }

    protected void addTrackToArtist(long artistId, Audio audio){
        addTrackToMap(songsByArtistId, artistId, audio);
    }

    protected void addTrackWithoutAlbumToArtist(long artistId, Audio audio){
        addTrackToMap(songsWithoutAlbumByArtistId, artistId, audio);
    }

    private void addAlbumToArtist(Album album, Artist artist){
        Long albumId = (long)album.getId();
        Long artistId = (long)artist.getId();
        List<Album> albums = albumsByArtistId.get(artistId);
        if(albums == null){
            albums = new ArrayList<Album>();
            albumsByArtistId.put(artistId,albums);
        }

        albums.add(album);
    }

    private void removeAlbumFromArtist(long albumId, long artistId){
        List<Album> albums = albumsByArtistId.get(artistId);
        albums.remove(albumId);
    }

    private boolean checkAlbumValidationAndWriteItIfSuccess(Artist artist, Album album){
        Long artistId = albumIdArtistId.get(album.getId());
        // remove albums, that have more that one artist
        if(artistId != null && artistId != artist.getId()){
            removeAlbum(album.getId());
            return false;
        } else if(artistId == null) {
            addAlbumToArtist(album,artist);
            albumIdArtistId.put((long)album.getId(), (long)artist.getId());
        }

        return true;
    }

    protected boolean writeAudioAndAlbumUsingArtist(Audio audio, Album album, Artist artist){
        boolean albumIsOk = checkAlbumValidationAndWriteItIfSuccess(artist, album);

        if(albumIsOk){
            String albumName = album.getName();
            audio.setAlbumName(albumName);
            audio.setAlbumId(album.getId());
            album.setArtistId(artist.getId());
            addTrackToAlbum(album.getId(),audio);
        } else {
            addTrackWithoutAlbumToArtist(artist.getId(),audio);
        }

        String artistName = artist.getName();
        audio.setArtistName(artistName);
        audio.setArtistId(artist.getId());
        addTrackToArtist(artist.getId(),audio);

        return albumIsOk;
    }

    protected void removeAlbum(long albumId){
        Long artistId = albumIdArtistId.get(albumId);
        if(artistId != null){
            albumIdArtistId.remove(albumId);
            removeAlbumFromArtist(albumId,artistId);
        }
    }

    protected final void executeInitPlayListsIfNeed(){
        if(playListsById == null){
            playListsById = new LinkedHashMap<Long, PlayList>();
            initPlayLists();
        }
    }

    protected final void executeInitAudiosIfNeed(){
        if(songsById == null){
            songsById = new LinkedHashMap<Long, Audio>();
            initAudios();
        }
    }

    @Override
    public List<PlayList> getPlayLists() {
        return new ArrayList<PlayList>(playListsById.values());
    }

    @Override
    public List<Album> getAlbums() {
        return new ArrayList<Album>(albumsById.values());
    }

    @Override
    public List<Audio> getSongs() {
        return new ArrayList<Audio>(songsById.values());
    }

    private <T> List<T> getElementsOf(Identified identified, Map<Long,List<T>> from){
        checkLocal(identified);

        Long id = (long)identified.getId();
        List<T> elements = from.get(id);
        if(elements == null){
            return Collections.emptyList();
        }

        return elements;
    }

    @Override
    public List<Audio> getSongsOfArtist(Artist artist) {
        return getElementsOf(artist, songsByArtistId);
    }

    @Override
    public List<Audio> getSongsWithoutAlbumOfArtist(Artist artist) {
        return getElementsOf(artist, songsWithoutAlbumByArtistId);
    }

    @Override
    public List<Artist> getArtists() {
        return new ArrayList<Artist>(artistsById.values());
    }

    private void checkLocal(Identified identified){
        if(!identified.isLocal()){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Audio> getSongsOfAlbum(Album album) {
        return getElementsOf(album, songsByAlbumId);
    }

    @Override
    public List<Audio> getSongsOfPlayList(PlayList playList) {
        return getElementsOf(playList,songsByPlayListId);
    }

    @Override
    public List<Album> getAlbumsOfArtist(Artist artist) {
        return getElementsOf(artist,albumsByArtistId);
    }

    @Override
    public boolean artistHasTracks(Artist artist) {
        return !songsByArtistId.isEmpty();
    }

    @Override
    public boolean artistHasAlbums(Artist artist) {
        List albums = albumsByArtistId.get(artist.getId());
        if(albums == null){
            return false;
        } else {
            return !albums.isEmpty();
        }
    }

    protected abstract void initAudios();
    protected abstract void initPlayLists();

    protected String getAlbumArtUrl(long albumId){
        return null;
    }

    private void albumArtUpdateAction(){
        for(Map.Entry<Long,Album> entry : albumsById.entrySet()){
            long id = entry.getKey();
            Album album = entry.getValue();

            String artUrl = getAlbumArtUrl(id);
            album.setUrlForAllArts(artUrl);

            List<Audio> audiosOfAlbum = songsByAlbumId.get(id);
            for(Audio audio : audiosOfAlbum){
                audio.setUrlForAllArts(artUrl);
            }
        }
    }

    @Override
    public void startAlbumArtsUpdating(final OnArtsUpdatingFinished onArtsUpdatingFinishedListener) {
        OnComplete onComplete = new OnComplete() {
            @Override
            public void onFinish() {
                if(onArtsUpdatingFinishedListener != null){
                    onArtsUpdatingFinishedListener.onFinished();
                }
            }
        };

        Threading.runOnBackground(new Runnable() {
            @Override
            public void run() {
                albumArtUpdateAction();
            }
        }, onComplete);
    }

    @Override
    public Audio getSongById(int id) {
        return songsById.get((long)id);
    }
}
