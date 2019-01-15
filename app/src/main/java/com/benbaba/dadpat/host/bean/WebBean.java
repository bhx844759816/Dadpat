package com.benbaba.dadpat.host.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/10/17.
 */
public class WebBean implements Parcelable {
    private String webPath;
    private int resDrawable;
    private int index;

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public int getResDrawable() {
        return resDrawable;
    }

    public void setResDrawable(int resDrawable) {
        this.resDrawable = resDrawable;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webPath);
        dest.writeInt(this.resDrawable);
        dest.writeInt(this.index);
    }

    public WebBean() {
    }

    protected WebBean(Parcel in) {
        this.webPath = in.readString();
        this.resDrawable = in.readInt();
        this.index = in.readInt();
    }

    public static final Creator<WebBean> CREATOR = new Creator<WebBean>() {
        @Override
        public WebBean createFromParcel(Parcel source) {
            return new WebBean(source);
        }

        @Override
        public WebBean[] newArray(int size) {
            return new WebBean[size];
        }
    };
}
