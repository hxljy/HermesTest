package com.hxl.hermes.core.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class Request implements Parcelable{
    //请求的对象  RequestBean 对应的json字符串
    private String data;
    //请求对象的类型
    private int type;

    public Request(String data, int type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    protected Request(Parcel in) {
        data = in.readString();
        type = in.readInt();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeInt(type);
    }

    @Override
    public String toString() {
        return "Request{" +
                "data='" + data + '\'' +
                ", type=" + type +
                '}';
    }
}
