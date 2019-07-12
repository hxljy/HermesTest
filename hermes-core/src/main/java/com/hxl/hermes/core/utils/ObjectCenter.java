package com.hxl.hermes.core.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/29 0029.
 */
public class ObjectCenter {

    private static final ObjectCenter ourInstance = new ObjectCenter();

    public static ObjectCenter getInstance() {
        return ourInstance;
    }

    private final ConcurrentHashMap<String, Object> mObjects;

    private ObjectCenter() {
        mObjects = new ConcurrentHashMap<>();
    }

    public void put(Object instance){
        mObjects.put(instance.getClass().getName(), instance);
    }

    public Object get(String clazzName){
        return mObjects.get(clazzName);
    }


}
