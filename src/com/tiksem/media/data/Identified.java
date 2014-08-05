package com.tiksem.media.data;

public class Identified {
    private long id;
    private boolean isLocal;

    public Identified(boolean local) {
        isLocal = local;
    }

    public Identified(boolean local, int id) {
        this.id = id;
        isLocal = local;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    @Override
    protected void finalize() throws Throwable {

    }
}
