package com.tiksem.media;

import android.text.TextUtils;
import com.tiksem.media.data.*;
import com.tiksem.media.local.AudioDataBase;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.navigation.*;
import com.tiksem.media.search.navigation.albums.AlbumsNavigationList;
import com.tiksem.media.search.navigation.albums.ArtistAlbumsNavigationList;
import com.tiksem.media.search.navigation.artists.ArtistsNavigationList;
import com.tiksem.media.search.navigation.artists.MultiTagsArtistNavigationList;
import com.tiksem.media.search.navigation.artists.TagArtistsNavigationList;
import com.tiksem.media.search.navigation.songs.*;
import com.utils.framework.algorithms.Search;
import com.utils.framework.collections.NavigationList;
import com.utilsframework.android.ErrorListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 02.01.13
 * Time: 3:42
 * To change this template use File | Settings | File Templates.
 */
public class AudioDataManager {
    private static final int SIMILAR_TRACKS_PER_PAGE_COUNT = 30;
    private static final int GENRES_OF_COUNTRY_MAX_COUNT = 150;

    private AudioDataBase localAudioDataBase;
    private InternetSearchEngine internetSearchEngine;

    private ErrorListener errorListener = new ErrorListener() {
        @Override
        public void onError(Throwable e) {

        }
    };

    public AudioDataManager(AudioDataBase localAudioDataBase, InternetSearchEngine internetSearchEngine) {
        this.localAudioDataBase = localAudioDataBase;
        this.internetSearchEngine = internetSearchEngine;
    }

    public List<Audio> getSongs(){
        return localAudioDataBase.getSongs();
    }

    public NavigationList<Audio> getSongs(String query){
        List<Audio> localAudios = localAudioDataBase.getSongs();
        PageNavListParams initParams = getPageNavigationListInitialParams(localAudios, query);

        SongsNavigationList songs = new SongsNavigationList(initParams);
        songs.setErrorListener(errorListener);
        return songs;
    }

    public List<Audio> getSongs(String query, int maxCount){
        if(maxCount == 0){
            return new ArrayList<Audio>();
        }

        if(maxCount < 0){
            throw new IllegalArgumentException();
        }

        try {
            return internetSearchEngine.searchAudios(query, maxCount, 0).elements;
        } catch (IOException e) {
            return new ArrayList<Audio>();
        }
    }

    public List<PlayList> getPlayLists(){
        List<PlayList> result = new ArrayList<PlayList>();

        List<PlayList> specialPlayLists = PlayLists.getSpecialPlayLists();
        List<PlayList> localPlayLists = localAudioDataBase.getPlayLists();

        result.addAll(specialPlayLists);
        result.addAll(localPlayLists);

        return result;
    }

    public List<PlayList> getPlayLists(String query, int maxCount){
        List<PlayList> playLists = getPlayLists();
        return Search.filter(playLists, query, maxCount);
    }

    private <T> PageNavListParams getPageNavigationListInitialParams(
            List<T> localElements,
            String query)
    {
        PageNavListParams initParams = new PageNavListParams();
        initParams.query = query;
        initParams.internetSearchEngine = internetSearchEngine;

        return initParams;
    }

    public List<Album> getAlbums(){
        return localAudioDataBase.getAlbums();
    }

    public List<Album> getAlbums(String query, int maxCount){
        if(maxCount == 0){
            return new ArrayList<Album>();
        }

        if(maxCount < 0){
            throw new IllegalArgumentException();
        }

        try {
            return internetSearchEngine.searchAlbums(query, maxCount, 0).elements;
        } catch (IOException e) {
            return new ArrayList<Album>();
        }
    }

    public NavigationList<Album> getAlbums(String query){
        List<Album> localAlbums = getAlbums();

        PageNavListParams initParams = getPageNavigationListInitialParams(localAlbums, query);

        AlbumsNavigationList albums = new AlbumsNavigationList(initParams);
        albums.setErrorListener(errorListener);
        return albums;
    }

    public List<Audio> getTracksOfAlbum(Album album) throws IOException {
        if(album.isLocal()){
            return localAudioDataBase.getSongsOfAlbum(album);
        } else {
            return internetSearchEngine.getSongsOfAlbum(album);
        }
    }

    public List<Audio> getTracksOfArtist(String artistName, int maxCount){
        if(maxCount == 0){
            return new ArrayList<Audio>();
        }

        if(maxCount < 0){
            throw new IllegalArgumentException();
        }

        try {
            return internetSearchEngine.getSongsOfArtist(artistName, maxCount, 0).elements;
        } catch (IOException e) {
            return new ArrayList<Audio>();
        }
    }

    public NavigationList<Audio> getTracksOfArtist(Artist artist){
        if(artist.isLocal()){
            List<Audio> local = localAudioDataBase.getSongsOfArtist(artist);
            return NavigationList.decorate(local);
        } else {
            List<Audio> initialElements = Collections.emptyList();
            String artistName = artist.getName();
            if(artistName == null){
                return NavigationList.emptyList();
            }

            PageNavListParams params =
                    getPageNavigationListInitialParams(initialElements,artistName);
            return new ArtistSongsNavigationList(params);
        }
    }

    public NavigationList<Audio> getTracksByTag(String tag){
        PageNavListParams params =
                getPageNavigationListInitialParams(Collections.emptyList(), tag);
        return new TagSongsNavigationList(params);
    }

    public NavigationList<Artist> getArtistsByTag(String genre){
        PageNavListParams params =
                getPageNavigationListInitialParams(Collections.emptyList(), genre);
        return new TagArtistsNavigationList(params);
    }

    public List<Album> getAlbumsOfArtist(Artist artist){
        if(artist.isLocal()){
            return localAudioDataBase.getAlbumsOfArtist(artist);
        } else {
            List<Audio> initialElements = Collections.emptyList();
            String artistName = artist.getName();
            if(artistName == null){
                return NavigationList.emptyList();
            }

            PageNavListParams params =
                    getPageNavigationListInitialParams(initialElements,artistName);
            return new ArtistAlbumsNavigationList(params);
        }
    }

    private List<Audio> getSongsOfSpecialPlayList(PlayList playList){
        long id = playList.getId();
        if (id == PlayLists.SONGS_YOU_MAY_LIKE_PLAYLIST_ID) {
            return getSongsYouMayLike();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Audio> getTracksOfPlayList(PlayList playList){
        if(playList.isLocal()){
            return localAudioDataBase.getSongsOfPlayList(playList);
        } else {
            return getSongsOfSpecialPlayList(playList);
        }
    }

    public List<Artist> getArtists(){
        return localAudioDataBase.getArtists();
    }

    public List<Artist> getArtists(String query, int count){
        if(count == 0){
            return new ArrayList<Artist>();
        }

        if(count < 0){
            throw new IllegalArgumentException();
        }

        try {
            return internetSearchEngine.searchArtists(query, count, 0).elements;
        } catch (IOException e) {
            return new ArrayList<Artist>();
        }
    }

    public NavigationList<Artist> getArtists(String query){
        List<Artist> localArtists = getArtists();

        PageNavListParams initParams = getPageNavigationListInitialParams(localArtists, query);

        ArtistsNavigationList artists = new ArtistsNavigationList(initParams);
        artists.setErrorListener(errorListener);
        return artists;
    }

    public boolean artistHasTracks(Artist artist){
        if(!artist.isLocal()){
            return true;
        } else {
            return localAudioDataBase.artistHasTracks(artist);
        }
    }

    public boolean artistHasAlbums(Artist artist){
        if(!artist.isLocal()){
            // not implemented yet, some artists do not have albums
            return true;
        } else {
            return localAudioDataBase.artistHasAlbums(artist);
        }
    }

    public NavigationList<Audio> getSongsYouMayLike(){
        SongsYouMayLikeNavigationList.Params params = new SongsYouMayLikeNavigationList.Params();
        params.userPlaylist = getSongs();
        params.songsCountPerPage = SIMILAR_TRACKS_PER_PAGE_COUNT;
        params.internetSearchEngine = internetSearchEngine;

        return new SongsYouMayLikeNavigationList(params);
    }

    public List<CountryGenreDefinition> getGenresOfCountry(String countryName){
        List<String> tags = internetSearchEngine.
                searchTagsHandleErrors(countryName, GENRES_OF_COUNTRY_MAX_COUNT, 0).elements;

        List<CountryGenreDefinition> result = new ArrayList<CountryGenreDefinition>(tags.size());

        for(String tag : tags){
            int genreBeginIndex = tag.indexOf(countryName) + countryName.length() + 1;
            if(genreBeginIndex >= 0 && genreBeginIndex < tag.length() - 1){
                String genreCandidate = tag.substring(genreBeginIndex);

                if(Genres.genreExists(genreCandidate)){
                    CountryGenreDefinition countryGenreDefinition = new CountryGenreDefinition();
                    countryGenreDefinition.genreName = genreCandidate;
                    countryGenreDefinition.tagName = tag;
                    result.add(countryGenreDefinition);
                }

            }
        }

        return result;
    }

    MultiTagNavigationList.Params getMultiTagNavigationListParams(){
        MultiTagNavigationList.Params params = new MultiTagNavigationList.Params();
        params.internetSearchEngine = internetSearchEngine;
        return params;
    }

    MultiTagNavigationList.Params getCountryNavigationListParams(String countryName){
        MultiTagNavigationList.Params params = getMultiTagNavigationListParams();
        params.tags = Countries.getCountryTags(countryName);
        return params;
    }

    MultiTagNavigationList.Params getMoodNavigationListParams(String moodName){
        MultiTagNavigationList.Params params = getMultiTagNavigationListParams();
        params.tags = Mood.getMoodTags(moodName);
        return params;
    }

    public NavigationList<Audio> getSongsByCountry(String countryName){
        MultiTagNavigationList.Params params = getCountryNavigationListParams(countryName);
        return new MultiTagsSongsNavigationList(params);
    }

    public NavigationList<Artist> getArtistsByCountry(String countryName){
        MultiTagNavigationList.Params params = getCountryNavigationListParams(countryName);
        return new MultiTagsArtistNavigationList(params);
    }

    public NavigationList<Audio> getSongsByMood(String moodName){
        MultiTagNavigationList.Params params = getMoodNavigationListParams(moodName);
        return new MultiTagsSongsNavigationList(params);
    }

    public NavigationList<Artist> getArtistsByMood(String moodName){
        MultiTagNavigationList.Params params = getMoodNavigationListParams(moodName);
        return new MultiTagsArtistNavigationList(params);
    }

    public void startAlbumArtsUpdating(AudioDataBase.OnArtsUpdatingFinished onArtsUpdatingFinished){
        localAudioDataBase.startAlbumArtsUpdating(onArtsUpdatingFinished);
    }

    public List<Audio> getSongsByTag(Object tag) throws IOException {
        if(tag instanceof AllSongsTag){
            AllSongsTag allSongsTag = (AllSongsTag) tag;
            String query = allSongsTag.getQuery();
            if(TextUtils.isEmpty(query)){
                return getSongs();
            } else {
                return getSongs(query);
            }
        }

        if(tag instanceof Artist){
            return getTracksOfArtist((Artist) tag);
        }

        if(tag instanceof Album){
            return getTracksOfAlbum((Album) tag);
        }

        if(tag instanceof PlayList){
            return getTracksOfPlayList((PlayList) tag);
        }

        throw new RuntimeException("Tag is not supported");
    }

    public PlayList addPlayList(String name) {
        return localAudioDataBase.addPlayList(name);
    }

    public List<PlayList> getPlayListsWhereSongCanBeAdded(Audio audio) {
        return localAudioDataBase.getPlayListsWhereSongCanBeAdded(audio);
    }

    public Audio getSongById(long id) {
        return localAudioDataBase.getSongById(id);
    }

    public Album getAlbumById(long id) {
        return localAudioDataBase.getAlbumById(id);
    }

    public List<Artist> getSuggestedArtistsByTrackName(String trackName, int maxCount) {
        if(maxCount == 0){
            return new ArrayList<Artist>();
        }

        if(maxCount < 0){
            throw new IllegalArgumentException();
        }

        List<Artist> artists =
                internetSearchEngine.getSuggestedArtistsByTrackName(trackName, maxCount);
        return artists;
    }

    public Album getAlbumOfAudioFromInternet(Audio audio) {
        try {
            return internetSearchEngine.getAlbumByArtistNameAndTrackName(audio.getArtistName(),
                    audio.getName());
        } catch (IOException e) {
            return null;
        }
    }

    public boolean tryFillAlbum(Audio audio) {
        return internetSearchEngine.tryFillAlbum(audio);
    }
}
