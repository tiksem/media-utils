package com.tiksem.media.data;

import com.utils.framework.Comparators;

import java.util.Comparator;

/**
 *
 * User: stikhonenko
 * Date: 3/11/13
 * Time: 6:39 PM
 */
public final class AudioComparators {
    public static <T extends NamedData> Comparator<T> namedData(){
        return new Comparator<T>() {
            @Override
            public int compare(T a, T b) {
                String nameA = a.getName();
                String nameB = b.getName();
                return nameA.compareTo(nameB);
            }
        };
    }

    public static <T extends ArtCollection> Comparator<T> artCollectionByNameReverseOrder(){
        return Comparators.reverseComparator(AudioComparators.<T>namedData());
    }

    private static Comparator<Audio> audioArtistNameComparator(){
        return new Comparator<Audio>() {
            @Override
            public int compare(Audio a, Audio b) {
                String artistNameA = a.getArtistName();
                String artistNameB = b.getArtistName();
                return artistNameA.compareTo(artistNameB);
            }
        };
    }

    private static Comparator<Album> albumArtistNameComparator(){
        return new Comparator<Album>() {
            @Override
            public int compare(Album a, Album b) {
                String artistNameA = a.getArtistName();
                String artistNameB = b.getArtistName();

                if(artistNameA == artistNameB){
                    return 0;
                }

                if(artistNameA == null){
                    return -1;
                } else if(artistNameB == null) {
                    return 1;
                }

                return artistNameA.compareTo(artistNameB);
            }
        };
    }

    private static Comparator<Audio> audioTotalListeningDurationComparator(){
        return new Comparator<Audio>() {
            @Override
            public int compare(Audio a, Audio b) {
                return Float.compare(a.getTotalListeningDuration(), b.getTotalListeningDuration());
            }
        };
    }

    public static Comparator<Audio> audioByName(){
        return Comparators.comparatorCombination(AudioComparators.<Audio>namedData(),
                audioArtistNameComparator());
    }

    public static Comparator<Audio> audioByNameReverseOrder(){
        return Comparators.reverseComparator(audioByName());
    }

    public static Comparator<Audio> audioByArtistName(){
        return Comparators.comparatorCombination(audioArtistNameComparator(),
                AudioComparators.<Audio>namedData());
    }

    public static Comparator<Album> albumByArtistName(){
        return Comparators.comparatorCombination(albumArtistNameComparator(),
                AudioComparators.<Album>namedData());
    }

    public static Comparator<Audio> audioByArtistNameReverseOrder(){
        return Comparators.reverseComparator(audioByArtistName());
    }

    public static Comparator<Audio> audioByTotalListeningDuration(){
        return Comparators.comparatorCombination(audioTotalListeningDurationComparator(),
                audioByName());
    }

    public static Comparator<Audio> audioByTotalListeningDurationReverseOrder(){
        return Comparators.reverseComparator(audioByTotalListeningDuration());
    }

    private static Comparator<PlayList> playListsSpecialNotSpecialComparator(){
        return new Comparator<PlayList>() {
            @Override
            public int compare(PlayList a, PlayList b) {
                Boolean aIsLocal = a.isLocal();
                Boolean bIsLocal = b.isLocal();
                return aIsLocal.compareTo(bIsLocal);
            }
        };
    }

    public static Comparator<PlayList> playListsNameComparator(){
        return Comparators.comparatorCombination(playListsSpecialNotSpecialComparator(),
                AudioComparators.<PlayList>namedData());
    }
}
