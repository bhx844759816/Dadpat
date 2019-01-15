package com.benbaba.dadpat.host.bean.drum;

public class DrumBean2<T> {

    private HeaderBean header;
    private T body;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
