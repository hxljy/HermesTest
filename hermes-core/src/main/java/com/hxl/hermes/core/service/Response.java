package com.hxl.hermes.core.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class Response implements Parcelable{

    //    响应的对象
    private String data;

    public String getData(){
        return data;
    }

    public Response(String data) {
        this.data = data;
    }

    protected Response(Parcel in) {
        data = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

}
