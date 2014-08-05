package com.tiksem.media.data;

import com.utils.framework.Equals;
import com.utils.framework.HashCodeProvider;
import com.utils.framework.collections.SetWithPredicates;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * User: stikhonenko
 * Date: 2/28/13
 * Time: 7:23 PM
 */
public class AudioNameArtistNameUniqueSet extends SetWithPredicates<Audio> {
    private static boolean audioEquals(Audio a, Audio b){
        String aName = a.getName();
        String bName = b.getName();

        if(aName.equalsIgnoreCase(bName)){
            String aArtistName = a.getArtistName();
            String bArtistName = b.getArtistName();
            return aArtistName.equalsIgnoreCase(bArtistName);
        }

        return false;
    }

    private static int audioHashCode(Audio audio){
        String name = audio.getName().toLowerCase();
        String artistName = audio.getArtistName().toLowerCase();

        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (artistName != null ? artistName.hashCode() : 0);
        return result;
    }

    public AudioNameArtistNameUniqueSet(){
        this(new HashSet<Entry<Audio>>());
    }

    public AudioNameArtistNameUniqueSet(Set<Entry<Audio>> entrySet) {
        super(entrySet, new Equals<Audio>() {
            @Override
            public boolean equals(Audio a, Audio b) {
                return audioEquals(a,b);
            }
        },

        new HashCodeProvider<Audio>() {
            @Override
            public int getHashCodeOf(Audio audio) {
                return audioHashCode(audio);
            }
        });
    }
}
