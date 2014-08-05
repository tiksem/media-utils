package com.tiksem.media.data;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 04.11.12
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */
public class PlayList extends NamedData{
    protected PlayList(boolean local) {
        super(local);
    }

    protected PlayList(boolean local, int id) {
        super(local, id);
    }

    public static PlayList createLocalPlayList(int id){
        return new PlayList(true,id);
    }

    static PlayList createInternetPlayList(int id, String name){
        PlayList playList = new PlayList(false,id);
        playList.setName(name);
        return playList;
    }

    public static PlayList createLocalPlayList(){
        return new PlayList(true);
    }

    static PlayList createInternetPlayList(){
        return new PlayList(false);
    }
}
