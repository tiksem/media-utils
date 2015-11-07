package com.tiksem.media.local;

import android.os.AsyncTask;
import com.tiksem.media.data.*;
import com.utils.framework.*;
import com.utils.framework.collections.map.MultiMap;
import com.utils.framework.collections.map.SetValuesHashMultiMap;
import com.utils.framework.collections.map.ValueExistsException;
import com.utilsframework.android.threading.OnComplete;
import com.utilsframework.android.threading.Threading;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 09.02.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class MappedLocalAudioDataBase implements AudioDataBase {
    private Map<Long,Audio> songsById;
    private Map<String,Artist> artistsByName = new LinkedHashMap<String, Artist>();
    private Map<Long,Album> albumsById = new LinkedHashMap<Long, Album>();
    private Map<Long,PlayList> playListsById;

    private Map<Long,List<Audio>> songsByAlbumId =
            new LinkedHashMap<Long, List<Audio>>();
    private Map<String,List<Audio>> songsByArtistName =
            new LinkedHashMap<String, List<Audio>>();
    private MultiMap<String, Audio> songsWithoutAlbumByArtistName =
            new SetValuesHashMultiMap<>();
    private Map<Long,List<Audio>> songsByPlayListId =
            new LinkedHashMap<Long, List<Audio>>();

    private MultiMap<String, Album> albumsByArtistName = new SetValuesHashMultiMap<>();

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

    @Override
    public Artist getArtistByName(String artistName) {
        return artistsByName.get(artistName);
    }

    protected Artist getOrCreateArtistWithName(String artistName){
        Artist artist = getArtistByName(artistName);
        if(artist == null){
            artist = Artist.createLocalArtist();
            artistsByName.put(artistName, artist);
        }

        return artist;
    }

    @Override
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

    private <Id> void addTrackToMap(Map<Id, List<Audio>> map, Id id, Audio track){
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

    protected Artist addTrackToArtist(String artistName, Audio audio){
        addTrackToMap(songsByArtistName, artistName, audio);
        if (!artistsByName.containsKey(artistName)) {
            Artist artist = Artist.createLocalArtist();
            artist.setName(artistName);
            artistsByName.put(artistName, artist);
            return artist;
        }

        return null;
    }

    protected Artist removeTrackFromArtist(String artistName, Audio audio){
        List<Audio> audios = songsByArtistName.get(artistName);
        audios.remove(audio);
        if (audios.isEmpty()) {
            songsByArtistName.remove(artistName);
            return artistsByName.remove(artistName);
        }

        return null;
    }

    protected void addTrackWithoutAlbumToArtist(String artistName, Audio audio){
        songsWithoutAlbumByArtistName.put(artistName, audio);
    }

    private void addAlbumToArtist(Album album, Artist artist) {
        albumsByArtistName.put(artist.getName(), album);
    }

    private void removeAlbumFromArtist(Album album, String artistName){
        albumsByArtistName.remove(artistName, album);
    }

    private void removeAlbum(Album album){
        Long albumId = album.getId();
        List<Audio> audios = songsByAlbumId.get(albumId);
        if(audios != null){
            for(Audio audio : audios){
                audio.setAlbumId(-1);
                audio.setAlbumName(null);
            }
            songsByAlbumId.remove(albumId);
        }

        albumsByArtistName.removeAll(album.getArtistName());
        albumsById.remove(albumId);
    }

    protected boolean writeAudioAndAlbumUsingArtist(Audio audio, Album album, Artist artist){
        String artistName = artist.getName();
        boolean albumIsOk = album != null;
        if (albumIsOk && album.getArtistName() != null) {
            albumIsOk = album.getArtistName().equals(artistName);
        }

        if(albumIsOk){
            String albumName = album.getName();
            audio.setAlbumName(albumName);
            audio.setAlbumId(album.getId());
            album.setArtistId(artist.getId());
            album.setArtistName(artistName);
            addTrackToAlbum(album.getId(), audio);
            addAlbumToArtist(album, artist);
        } else {
            removeAlbum(album);
            addTrackWithoutAlbumToArtist(artistName, audio);
        }

        audio.setArtistName(artistName);
        audio.setArtistId(artist.getId());
        addTrackToArtist(artistName, audio);

        return albumIsOk;
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

    private <T, Id> List<T> getElementsOf(Id id, Map<Id,List<T>> from){
        List<T> elements = from.get(id);
        if(elements == null){
            elements = new ArrayList<T>();
            from.put(id, elements);
        }

        return elements;
    }

    private <T> List<T> getElementsOf(Identified identified, Map<Long,List<T>> from){
        checkLocal(identified);

        Long id = identified.getId();
        return getElementsOf(id, from);
    }

    @Override
    public List<Audio> getSongsOfArtist(Artist artist) {
        return getElementsOf(artist.getName(), songsByArtistName);
    }

    @Override
    public List<Audio> getSongsWithoutAlbumOfArtist(Artist artist) {
        return new ArrayList<>(songsWithoutAlbumByArtistName.getValues(artist.getName()));
    }

    @Override
    public List<Artist> getArtists() {
        return new ArrayList<Artist>(artistsByName.values());
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
        return new ArrayList<>(getElementsOf(playList,songsByPlayListId));
    }

    @Override
    public List<Album> getAlbumsOfArtist(Artist artist) {
        return new ArrayList<>(albumsByArtistName.getValues(artist.getName()));
    }

    @Override
    public boolean artistHasTracks(Artist artist) {
        return !songsByArtistName.isEmpty();
    }

    @Override
    public boolean artistHasAlbums(Artist artist) {
        return albumsByArtistName.containsKey(artist.getName());
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

            if (artUrl != null) {
                List<Audio> audiosOfAlbum = songsByAlbumId.get(id);
                for(Audio audio : audiosOfAlbum){
                    if (audio.getArtUrl(ArtSize.SMALL) == null) {
                        audio.setUrlForAllArts(artUrl);
                    }
                }
            }
        }
    }

    @Override
    public AsyncTask startAlbumArtsUpdating(final OnArtsUpdatingFinished onArtsUpdatingFinishedListener) {
        OnComplete onComplete = new OnComplete() {
            @Override
            public void onFinish() {
                if(onArtsUpdatingFinishedListener != null){
                    onArtsUpdatingFinishedListener.onFinished();
                }
            }
        };

        return Threading.runOnBackground(new Runnable() {
            @Override
            public void run() {
                albumArtUpdateAction();
            }
        }, onComplete);
    }

    @Override
    public Audio getSongById(long id) {
        return songsById.get((long)id);
    }

    @Override
    public synchronized PlayList addPlayList(String name) {
        if(getPlaylistByName(name) != null){
            throw new IllegalArgumentException("Playlist exists");
        }

        PlayList playList = PlayList.createLocalPlayList();
        playList.setName(name);
        addPlayListToDatabase(playList);
        playListsById.put(playList.getId(), playList);
        return playList;
    }

    protected abstract void addPlayListToDatabase(PlayList playList);
    protected abstract void addAudioToPlayListInDatabase(PlayList playList, Audio audio);
    protected abstract void removeAudioFromPlayListInDatabase(PlayList playList, Audio audio);

    @Override
    public List<PlayList> getPlayListsWhereSongCanBeAdded(final Audio audio) {
        return CollectionUtils.findAll(getPlayLists(), new Predicate<PlayList>() {
            @Override
            public boolean check(PlayList playList) {
                return !getSongsOfPlayList(playList).contains(audio);
            }
        });
    }

    @Override
    public synchronized boolean addSongToPlayList(Audio audio, PlayList playList) {
        Long playListId = playList.getId();
        List<Audio> audiosOfPlayList = songsByPlayListId.get(playListId);
        if(audiosOfPlayList == null){
            audiosOfPlayList = new ArrayList<Audio>();
            songsByPlayListId.put(playListId, audiosOfPlayList);
        }

        if(audiosOfPlayList.contains(audio)){
            return false;
        }

        audiosOfPlayList.add(audio);
        addAudioToPlayListInDatabase(playList, audio);

        return true;
    }

    @Override
    public synchronized boolean removeSongFromPlayList(Audio audio, PlayList playList) {
        Long playListId = playList.getId();
        List<Audio> audiosOfPlayList = songsByPlayListId.get(playListId);
        if(audiosOfPlayList == null){
            return false;
        }

        if(!audiosOfPlayList.remove(audio)){
            return false;
        }

        removeAudioFromPlayListInDatabase(playList, audio);

        return true;
    }

    protected final PlayList getPlaylistByName(final String name) {
        if(name == null){
            throw new NullPointerException();
        }

        return CollectionUtils.find(getPlayLists(), new Predicate<PlayList>() {
            @Override
            public boolean check(PlayList playList) {
                return name.equals(playList.getName());
            }
        });
    }
}
