package com.tiksem.media.data;

/**
 * Created by CM on 8/29/2014.
 */
public final class AllSongsTag {
    private String query;

    public AllSongsTag(String query) {
        this.query = query;
    }

    public AllSongsTag() {
        this.query = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllSongsTag that = (AllSongsTag) o;

        if (query != null ? !query.equals(that.query) : that.query != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return query != null ? query.hashCode() : 0;
    }

    public String getQuery() {
        return query;
    }
}
