package com.tiksem.media.data;

public class Identified {
    private long id = -1;
    private boolean isLocal;

    public Identified(boolean local) {
        isLocal = local;
    }

    public Identified(boolean local, long id) {
        this.id = id;
        isLocal = local;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identified that = (Identified) o;

        if (id != that.id) return false;
        if (isLocal != that.isLocal) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (isLocal ? 1 : 0);
        return result;
    }
}
