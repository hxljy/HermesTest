package com.hxl.hermes.core.utils;

import android.text.TextUtils;

import com.hxl.hermes.core.request.RequestBean;
import com.hxl.hermes.core.request.RequestParameter;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class TypeCenter {

    private static final TypeCenter ourInstance = new TypeCenter();

    public static TypeCenter getInstance() {
        return ourInstance;
    }

    private final ConcurrentHashMap<String, Class<?>> mAnnotatedClasses;
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;

    private TypeCenter(){
        mAnnotatedClasses = new ConcurrentHashMap<>();
        mRawMethods = new ConcurrentHashMap<>();
    }

    public void register(Class<?> clazz){
        registerClass(clazz);
        registerMethod(clazz);
    }

    private void registerMethod(Class<?> clazz){
        mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
        ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String key = TypeUtils.getMethodId(method);
            map.put(key, method);
        }
    }

    private void registerClass(Class<?> clazz){
        String className = clazz.getName();
        mAnnotatedClasses.putIfAbsent(className, clazz);
    }


    public Class<?> getClassType(String name)   {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Class<?> clazz = mAnnotatedClasses.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }

    public Method getMethod(Class<?> clazz, RequestBean requestBean) {
        String name = requestBean.getMethodName();
        if (name != null){
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> methods = mRawMethods.get(clazz);
            Method method = methods.get(name);
            if (method != null){
                return method;
            }
            int pos = name.indexOf("(");
            Class[] paramters = null;
            RequestParameter[] requestParameters = requestBean.getRequestParameters();
            if (requestParameters != null && requestParameters.length > 0){
                paramters = new Class[requestParameters.length];
                for (int i = 0; i < requestParameters.length; i++) {
                    paramters[i] = getClassType(requestParameters[i].getParameterClassName());
                }
            }
            method = TypeUtils.getMethod(clazz, name.substring(0, pos), paramters);
            methods.put(name, method);
            return method;
        }
        return null;
    }

}
