package com.tiksem.media.data;

import android.os.Parcel;
import com.utils.framework.CollectionUtils;
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
    private String name;

    public void cloneDataFrom(NamedData other) {
        super.cloneDataFrom(other);
        this.name = other.name;
    }

    public NamedData(NamedData other) {
        super(other);
        this.name = other.name;
    }

    public static <T extends NamedData> Equals<T> equalsIgnoreCase() {
        return new Equals<T>() {
            @Override
            public boolean equals(T a, T b) {
                return a.getName().equalsIgnoreCase(b.getName());
            }
        };
    }

    public static <T extends NamedData> Equals<T> nameEquals() {
        return new Equals<T>() {
            @Override
            public boolean equals(T a, T b) {
                return a.getName().equals(b.getName());
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

    public static HashCodeProvider nameHashCodeProvider() {
        return new HashCodeProvider() {
            @Override
            public int getHashCodeOf(Object object) {
                return ((NamedData)object).getName().hashCode();
            }
        };
    }

    public static <T extends NamedData> List<T> uniqueNames(List<T> list) {
        return CollectionUtils.unique(list, NamedData.<T>nameEquals(),
                NamedData.<T>nameHashCodeProvider());
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
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
    }

    protected NamedData(Parcel in) {
        super(in);
        this.name = in.readString();
    }

}

