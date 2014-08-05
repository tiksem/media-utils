package com.tiksem.media.data;

public class Artist extends ArtCollection{
    private Artist(int id, boolean local) {
        super(id, local);
    }

    private Artist(boolean local) {
        super(local);
    }

    public static Artist createLocalArtist(int id){
        return new Artist(id, true);
    }

    public static Artist createInternetArtist(int id){
        return new Artist(id, false);
    }

    public static Artist createLocalArtist(){
        return new Artist(true);
    }

    public static Artist createInternetArtist(){
        return new Artist(false);
    }
}
