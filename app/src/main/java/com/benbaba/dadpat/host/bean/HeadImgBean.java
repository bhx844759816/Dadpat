package com.benbaba.dadpat.host.bean;

public class HeadImgBean {

    private String url;//头像的url

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "HeadImgBean{" +
                "url='" + url + '\'' +
                '}';
    }
}
