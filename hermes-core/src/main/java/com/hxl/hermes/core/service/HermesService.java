package com.hxl.hermes.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hxl.hermes.core.Hermes;
import com.hxl.hermes.core.response.InstanceResponseMake;
import com.hxl.hermes.core.response.ResponseMake;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class HermesService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private SunService.Stub mBinder = new SunService.Stub() {
        @Override
        public Response send(Request request)  {
            ResponseMake responseMake = null;
            switch (request.getType()){
                case Hermes.TYPE_GET:
                    //获取单例
                    responseMake = new InstanceResponseMake();
                    break;
//                case Hermes.TYPE_NEW:
//                    responseMake = new ObjectResponseMake();
//                    break;
            }
            if (responseMake != null){
                return responseMake.makeResponse(request);
            }
            return null;
        }
    };
}
