package com.tiksem.media.data;

import com.utils.framework.collections.cache.GlobalStringCache;

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

    public NamedData(boolean local) {
        super(local);
    }

    public NamedData(boolean local, int id) {
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
}
