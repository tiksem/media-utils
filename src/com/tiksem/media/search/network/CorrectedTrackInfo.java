package com.tiksem.media.search.network;

/**
* Created with IntelliJ IDEA.
* User: Администратор
* Date: 05.07.13
* Time: 2:29
* To change this template use File | Settings | File Templates.
*/
public class CorrectedTrackInfo {
    public String name;
    public String artistName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CorrectedTrackInfo that = (CorrectedTrackInfo) o;

        if (artistName != null ? !artistName.equals(that.artistName) : that.artistName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (artistName != null ? artistName.hashCode() : 0);
        return result;
    }
}
