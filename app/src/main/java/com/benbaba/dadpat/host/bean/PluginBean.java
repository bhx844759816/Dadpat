package com.benbaba.dadpat.host.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.benbaba.dadpat.host.view.WaveDrawable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PluginBean implements Parcelable {

    @SerializedName("apkVersion")
    private int version;
    @SerializedName("apkIsVersionName")
    private String versionName;
    @SerializedName("apkEnglish")
    private String pluginName;
    @SerializedName("apkAlias")
    private String pluginAlias;
    @SerializedName("apkClassName")
    private String mainClass;
    @SerializedName("jdUrl")
    private String url;
    @SerializedName("apkMd5")
    private String apkMd5;
    @SerializedName("apkCoverBefore")
    private String imageGray;
    @SerializedName("apkCoverEnd")
    private String image;
    @SerializedName("apkSize")
    private String apkSize;
    @SerializedName("apkIsRelease")
    private String isRelease;// 是否发布
    private transient boolean isInstall; // 是否已经安装
    private transient boolean isNeedUpdate;// 是否需要更新
    private transient boolean isDownLanding;//正在下载
    private transient String savePath;
    private transient WaveDrawable waveDrawable;
    private transient int imgRes; // 背景图

    @Override
    public String toString() {
        return "PluginBean{" +
                "version=" + version +
                ", versionName='" + versionName + '\'' +
                ", pluginName='" + pluginName + '\'' +
                ", pluginAlias='" + pluginAlias + '\'' +
                ", mainClass='" + mainClass + '\'' +
                ", url='" + url + '\'' +
                ", apkMd5='" + apkMd5 + '\'' +
                ", imageGray='" + imageGray + '\'' +
                ", image='" + image + '\'' +
                ", apkSize='" + apkSize + '\'' +
                ", isRelease='" + isRelease + '\'' +
                ", isInstall=" + isInstall +
                ", isNeedUpdate=" + isNeedUpdate +
                ", isDownLanding=" + isDownLanding +
                ", savePath='" + savePath + '\'' +
                ", waveDrawable=" + waveDrawable +
                ", imgRes=" + imgRes +
                '}';
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginAlias() {
        return pluginAlias;
    }

    public void setPluginAlias(String pluginAlias) {
        this.pluginAlias = pluginAlias;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String apkMd5) {
        this.apkMd5 = apkMd5;
    }

    public String getImageGray() {
        return imageGray;
    }

    public void setImageGray(String imageGray) {
        this.imageGray = imageGray;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public String getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(String isRelease) {
        this.isRelease = isRelease;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean install) {
        isInstall = install;
    }

    public boolean isNeedUpdate() {
        return isNeedUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }

    public boolean isDownLanding() {
        return isDownLanding;
    }

    public void setDownLanding(boolean downLanding) {
        isDownLanding = downLanding;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public WaveDrawable getWaveDrawable() {
        return waveDrawable;
    }

    public void setWaveDrawable(WaveDrawable waveDrawable) {
        this.waveDrawable = waveDrawable;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.version);
        dest.writeString(this.versionName);
        dest.writeString(this.pluginName);
        dest.writeString(this.pluginAlias);
        dest.writeString(this.mainClass);
        dest.writeString(this.url);
        dest.writeString(this.apkMd5);
        dest.writeString(this.imageGray);
        dest.writeString(this.image);
        dest.writeString(this.apkSize);
        dest.writeString(this.isRelease);
    }

    public PluginBean() {
    }

    protected PluginBean(Parcel in) {
        this.version = in.readInt();
        this.versionName = in.readString();
        this.pluginName = in.readString();
        this.pluginAlias = in.readString();
        this.mainClass = in.readString();
        this.url = in.readString();
        this.apkMd5 = in.readString();
        this.imageGray = in.readString();
        this.image = in.readString();
        this.apkSize = in.readString();
        this.isRelease = in.readString();
    }

    public static final Creator<PluginBean> CREATOR = new Creator<PluginBean>() {
        @Override
        public PluginBean createFromParcel(Parcel source) {
            return new PluginBean(source);
        }

        @Override
        public PluginBean[] newArray(int size) {
            return new PluginBean[size];
        }
    };
}
