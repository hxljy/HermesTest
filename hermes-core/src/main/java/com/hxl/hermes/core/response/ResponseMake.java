package com.hxl.hermes.core.response;

import com.google.gson.Gson;
import com.hxl.hermes.core.utils.ObjectCenter;
import com.hxl.hermes.core.utils.TypeCenter;
import com.hxl.hermes.core.request.RequestBean;
import com.hxl.hermes.core.request.RequestParameter;
import com.hxl.hermes.core.service.Request;
import com.hxl.hermes.core.service.Response;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public abstract class ResponseMake {

    Class<?> resultClass;

    Object[] mParameters;

    Gson gson = new Gson();

    TypeCenter typeCenter = TypeCenter.getInstance();

    ObjectCenter objectCenter = ObjectCenter.getInstance();

    protected abstract Object invokeMethod();

    protected abstract void setMethod(RequestBean requestBean);

    public Response makeResponse(Request request){
        //1. 取出request中的requestBean消息并转换为requestBean。
        RequestBean requestBean = gson.fromJson(request.getData(), RequestBean.class);

        //2. 通过requestBean中设置的目标单例类的名字去加载类。
        resultClass = typeCenter.getClassType(requestBean.getResultClassName());

        //3. 通过requestBean中的设置的方法名获取到要执行的具体方法。
        setMethod(requestBean);

        //4. 组装参数，将参数进行还原组装。
        RequestParameter[] requestParameters = requestBean.getRequestParameters();
        if (requestParameters != null && requestParameters.length > 0){
            mParameters = new Object[requestParameters.length];
            for (int i = 0; i < requestParameters.length; i++) {
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                mParameters[i] = gson.fromJson(requestParameter.getParameterValue(), clazz);
            }
        }else{
            mParameters = new Object[0];
        }

        //5. 执行方法，并得到执行结果
        Object resultObj = invokeMethod();

        //6. 将执行结果封装为Response返回给进行B
        ResponseBean responseBean = new ResponseBean(resultObj);
        return new Response(gson.toJson(responseBean));
    }



}
