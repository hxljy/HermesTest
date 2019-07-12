package com.hxl.hermes.core.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hxl.hermes.core.Hermes;
import com.hxl.hermes.core.response.ResponseBean;
import com.hxl.hermes.core.service.HermesService;
import com.hxl.hermes.core.service.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class HermesInvocationHandler implements InvocationHandler {

    private Class clazz;
    private static Gson gson = new Gson();
    private Class hermesService;

    public HermesInvocationHandler(Class<? extends HermesService> service, Class clazz){
        this.clazz = clazz;
        this.hermesService = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Response responce= Hermes.getDefault().sendObjectRequest(hermesService, clazz, method, args);
        if (!TextUtils.isEmpty(responce.getData())){
            ResponseBean responceBean = gson.fromJson(responce.getData(), ResponseBean.class);
            if (responceBean.getData() != null){
                String data = gson.toJson(responceBean.getData());
                Class<?> returnType = method.getReturnType();
                return gson.fromJson(data, returnType);
            }
        }
        return null;
    }
}
