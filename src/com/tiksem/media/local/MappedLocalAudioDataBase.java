package com.tiksem.media.local;

import android.graphics.Bitmap;
import com.tiksem.media.data.*;
import com.utils.framework.CollectionUtils;
import com.utils.framework.Predicate;
import com.utils.framework.collections.map.ListValuesMultiMap;
import com.utils.framework.collections.map.MultiMap;
import com.utils.framework.collections.map.SetValuesHashMultiMap;
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

    @Override
    public Artist getArtistById(long id){
        return artistsById.get(id);
    }

    protected void removeArtistWithId(long id) {
        artistsById.remove(id);
    }

    protected Artist getOrCreateArtistWithId(long id){
        Artist artist = getArtistById(id);
        if(artist == null){
            artist = Artist.createLocalArtist((int)id);
            artistsById.put(id,artist);
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
        Long artistId = artist.getId();
        List<Album> albums = albumsByArtistId.get(artistId);
        if(albums == null){
            albums = new ArrayList<Album>();
            albumsByArtistId.put(artistId,albums);
        }

        albums.add(album);
    }

    private void removeAlbumFromArtist(final long albumId, long artistId){
        List<Album> albums = albumsByArtistId.get(artistId);
        CollectionUtils.removeAll(albums, new Predicate<Album>() {
            @Override
            public boolean check(Album item) {
                return item.getId() == albumId;
            }
        });
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
        boolean albumIsOk = album != null && checkAlbumValidationAndWriteItIfSuccess(artist, album);

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
        List<Audio> audios = songsByAlbumId.get(albumId);
        if(audios != null){
            for(Audio audio : audios){
                audio.setAlbumId(-1);
                audio.setAlbumName(null);
                setAlbumIdToAudioInDataBase(-1l, audio.getId());
            }
            songsByAlbumId.remove(albumId);
        }

        Long artistId = albumIdArtistId.get(albumId);
        if(artistId != null){
            albumIdArtistId.remove(albumId);
            removeAlbumFromArtist(albumId,artistId);
        }

        albumsById.remove(albumId);
        removeAlbumFromDataBase(albumId);
    }

    protected final void executeInitPlayListsIfNeed(){
        if(playListsById == null){
            playListsById = new LinkedHashMap<Long, PlayList>();
            initPlayLists();
            for (Artist artist : getArtists()) {
                removeAlbumsWithSameNames(artist);
            }
        }
    }

    private void removeAlbumsWithSameNames(Artist artist) {
        MultiMap<String, Album> stringAlbumMultiMap = new ListValuesMultiMap<String, Album>();
        List<Album> albums = getAlbumsOfArtist(artist);
        for(Album album : albums){
            stringAlbumMultiMap.put(album.getName().toLowerCase(), album);
        }

        Collection<String> keys = stringAlbumMultiMap.getKeys();
        for(String key : keys){
            Collection<Album> albumsByName = stringAlbumMultiMap.getValues(key);
            if(albumsByName.size() <= 1){
                continue;
            }

            Album bestAlbum = Collections.max(albumsByName, new Comparator<Album>() {
                @Override
                public int compare(Album a, Album b) {
                    String aArtUrl = a.getArtUrl(ArtSize.SMALL);
                    String bArtUrl = b.getArtUrl(ArtSize.SMALL);

                    if(aArtUrl == bArtUrl || (aArtUrl != null && bArtUrl != null)){
                        Collection<Audio> aSongs = songsByAlbumId.get(a.getId());
                        Collection<Audio> bSongs = songsByAlbumId.get(b.getId());
                        int aSize = aSongs == null ? 0 : aSongs.size();
                        int bSize = bSongs == null ? 0 : bSongs.size();
                        return aSize - bSize;
                    } else {
                        if(aArtUrl != null){
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            });

            LinkedHashSet<Audio> audiosOfAlbums = new LinkedHashSet<Audio>();
            long bestAlbumId = bestAlbum.getId();
            for(Album album : albumsByName){
                List<Audio> audios = songsByAlbumId.get(album.getId());
                audiosOfAlbums.addAll(audios);

                if(album != bestAlbum){
                    removeAlbum(album.getId());

                    for(Audio audio : audios){
                        audio.setAlbumId(bestAlbumId);
                        setAlbumIdToAudioInDataBase(bestAlbumId, audio.getId());
                    }
                }
            }

            songsByAlbumId.put(bestAlbumId, new ArrayList<Audio>(audiosOfAlbums));
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

    private <T> List<T> getElementsOf(long id, Map<Long,List<T>> from){
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
        return getElementsOf(artist, songsByArtistId);
    }

    protected final List<Audio> getSongsByArtistId(long artistId) {
        return getElementsOf(artistId, songsByArtistId);
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
        return getElementsOf(artist, albumsByArtistId);
    }

    protected final List<Album> getAlbumsByArtistId(long artistId) {
        return getElementsOf(artistId, albumsByArtistId);
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

    protected abstract String getArtistArtUrl(long artistId);

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

        for(Artist artist : getArtists()){
            long id = artist.getId();
            String artUrl = getArtistArtUrl(id);
            artist.setUrlForAllArts(artUrl);
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
    public Audio getSongById(long id) {
        return songsById.get((long)id);
    }

    @Override
    public PlayList addPlayList(String name) {
        if(getPlaylistByName(name) != null){
            throw new IllegalArgumentException("Playlist exists");
        }

        PlayList playList = PlayList.createLocalPlayList();
        playList.setName(name);
        addPlayListToDatabase(playList);
        playListsById.put(playList.getId(), playList);
        return playList;
    }

    @Override
    public Artist addArtist(String name) {
        if(getArtistByName(name) != null){
            throw new IllegalArgumentException("Artist exists");
        }

        Artist artist = Artist.createLocalArtist();
        artist.setName(name);
        addArtistToDatabase(artist);
        artistsById.put(artist.getId(), artist);
        return artist;
    }

    @Override
    public Album addAlbum(String albumName, String artistName) {
        Artist artist = getArtistByName(artistName);
        if(artist == null){
            throw new IllegalArgumentException("Could not find artist with '" + artistName + "' name");
        }

        long artistId = artist.getId();
        Album album = getAlbumByNameAndArtistId(albumName, artistId);
        if(album != null){
            throw new IllegalArgumentException("Album exists");
        }

        album = Album.createLocalAlbum();
        album.setName(albumName);
        album.setArtistName(artistName);
        album.setArtistId(artistId);
        addAlbumToDatabase(album);
        albumsById.put(album.getId(), album);

        List<Album> albums = albumsByArtistId.get(artistId);
        if(albums == null){
            albums = new ArrayList<Album>();
            albumsByArtistId.put(artistId, albums);
        }
        albums.add(album);

        return album;
    }

    protected abstract void addPlayListToDatabase(PlayList playList);
    protected abstract void addArtistToDatabase(Artist artist);
    protected abstract void addAlbumToDatabase(Album album);
    protected abstract void addAudioToPlayListInDatabase(PlayList playList, Audio audio);
    protected abstract void removeAlbumFromDataBase(long id);
    protected abstract void setAlbumIdToAudioInDataBase(Long albumId, long audioId);

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
    public boolean addSongToPlayList(Audio audio, PlayList playList) {
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

    protected final Artist getArtistByName(final String name) {
        if(name == null){
            throw new NullPointerException();
        }

        return CollectionUtils.find(getArtists(), new Predicate<Artist>() {
            @Override
            public boolean check(Artist artist) {
                return name.equals(artist.getName());
            }
        });
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

    protected final Album getAlbumByNameAndArtistId(final String albumName, long artistId) {
        if(albumName == null){
            throw new NullPointerException();
        }

        return CollectionUtils.find(getAlbumsByArtistId(artistId), new Predicate<Album>() {
            @Override
            public boolean check(Album album) {
                return albumName.equals(album.getName());
            }
        });
    }

    @Override
    public void commitAudioChangesToDataBase(Audio audio) {

    }

    @Override
    public void setAlbumArt(Bitmap bitmap, long albumId) {
        String path = saveAlbumArtToDataBase(bitmap, albumId);
        if(path != null){
            Album album = albumsById.get(albumId);
            if(album == null){
                throw new IllegalArgumentException("Could not find album with " + albumId + " id");
            }

            album.setUrlForAllArts(path);
            List<Audio> audios = songsByAlbumId.get(albumId);
            if (audios != null) {
                for(Audio audio : audios){
                    audio.setUrlForAllArts(path);
                }
            }
        }
    }

    @Override
    public void setArtistArt(Bitmap bitmap, long artistId) {
        Artist artist = artistsById.get(artistId);
        if(artist == null){
            throw new IllegalArgumentException("Could not find artist with " + artistId + " id");
        }

        String path = saveArtistArtToDataBase(bitmap, artistId);
        artist.setUrlForAllArts(path);
    }

    @Override
    public void setArt(Bitmap bitmap, ArtCollection artCollection) {
        if(artCollection instanceof Album){
            setAlbumArt(bitmap, artCollection.getId());
        } else if(artCollection instanceof Artist) {
            setArtistArt(bitmap, artCollection.getId());
        } else {
            throw new IllegalArgumentException("Unsuuported ArtCollection type " +
                    artCollection.getClass().getCanonicalName());
        }
    }

    protected abstract String saveAlbumArtToDataBase(Bitmap bitmap, long albumId);
    protected abstract String saveArtistArtToDataBase(Bitmap bitmap, long artistId);
}
