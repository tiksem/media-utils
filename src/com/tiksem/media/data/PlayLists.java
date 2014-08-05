package com.tiksem.media.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 09.03.13
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public final class PlayLists {
    public static final int SONGS_YOU_MAY_LIKE_PLAYLIST_ID = 1;

    private static final PlayList[] SPECIAL_PLAY_LISTS = new PlayList[]{
            PlayList.createInternetPlayList(SONGS_YOU_MAY_LIKE_PLAYLIST_ID, "Songs you may like")
    };

    public static List<PlayList> getSpecialPlayLists(){
        return Collections.unmodifiableList(Arrays.asList(SPECIAL_PLAY_LISTS));
    }
}
