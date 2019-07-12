package com.hxl.hermes.core.response;

import com.hxl.hermes.core.request.RequestBean;

import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class InstanceResponseMake extends ResponseMake {

    private Object instance;
    private Method mMethod;

    @Override
    protected Object invokeMethod() {
        Object object = null;
        try {
            object = mMethod.invoke(instance, mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    protected void setMethod(RequestBean requestBean) {
        try {
            instance = objectCenter.get(requestBean.getClassName());
            if (instance == null){
                Method getInstanceMethod = resultClass.getMethod("getInstance", new Class[]{});
                if (getInstanceMethod != null){
                    instance = getInstanceMethod.invoke(null);
                    objectCenter.put(instance);
                }
            }

            mMethod = typeCenter.getMethod(resultClass, requestBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
