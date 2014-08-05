package com.tiksem.media.data;

public class Identification{
    private Object id;
    private boolean local;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identification)) return false;

        Identification that = (Identification) o;

        if (local != that.local) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (local ? 1 : 0);
        return result;
    }

    public Identification(Object id, boolean local) {
        this.id = id;
        this.local = local;
    }

    public Identification(boolean local){
        this.local = local;
    }

    public Object getId() {
        return id;
    }

    public boolean isIdSet(){
        return id != null;
    }

    public void setId(Object id){
        this.id = id;
    }

    public void setIsLocal(boolean value){
        local = value;
    }

    public boolean isLocal(){
        return local;
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
