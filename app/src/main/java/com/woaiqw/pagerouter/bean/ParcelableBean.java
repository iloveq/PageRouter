package com.woaiqw.pagerouter.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by haoran on 2019-08-02.
 */
public class ParcelableBean implements Parcelable {

    public String type;

    public ParcelableBean(String type){
        this.type = type;
    }

    protected ParcelableBean(Parcel in) {
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableBean> CREATOR = new Creator<ParcelableBean>() {
        @Override
        public ParcelableBean createFromParcel(Parcel in) {
            return new ParcelableBean(in);
        }

        @Override
        public ParcelableBean[] newArray(int size) {
            return new ParcelableBean[size];
        }
    };
}
