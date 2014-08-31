package com.tiksem.media.data;

import com.utils.framework.Equals;
import com.utils.framework.HashCodeProvider;
import com.utils.framework.collections.cache.GlobalStringCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 03.07.13
 * Time: 5:55
 * To change this template use File | Settings | File Templates.
 */
public class NamedData extends Identified{
    private static final GlobalStringCache STRING_CACHE = GlobalStringCache.getInstance();
    private String name;

    public static <T extends NamedData> Equals<T> equalsIgnoreCase() {
        return new Equals<T>() {
            @Override
            public boolean equals(T a, T b) {
                return a.getName().equalsIgnoreCase(b.getName());
            }
        };
    }

    public static HashCodeProvider ignoreCaseHashCodeProvider() {
        return new HashCodeProvider() {
            @Override
            public int getHashCodeOf(Object object) {
                return ((NamedData)object).getName().toLowerCase().hashCode();
            }
        };
    }

    public NamedData(boolean local) {
        super(local);
    }

    public NamedData(boolean local, long id) {
        super(local, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = STRING_CACHE.putOrGet(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NamedData namedData = (NamedData) o;

        if (name != null ? !name.equals(namedData.name) : namedData.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public static <T extends NamedData> List<String> namedDataListToNameList(List<T> namedDatas) {
        List<String> result = new ArrayList<String>(namedDatas.size());
        for(NamedData namedData : namedDatas){
            result.add(namedData.getName());
        }

        return result;
    }

    public static <T extends NamedData> List<String> namedDataListToLowerCaseNameList(List<T> namedDatas) {
        List<String> result = new ArrayList<String>(namedDatas.size());
        for(NamedData namedData : namedDatas){
            result.add(namedData.getName().toLowerCase());
        }

        return result;
    }
}

