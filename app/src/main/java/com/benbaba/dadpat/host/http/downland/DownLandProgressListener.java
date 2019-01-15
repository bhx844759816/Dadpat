package com.benbaba.dadpat.host.http.downland;

/**
 * Created by Administrator on 2018/2/10.
 */
public interface DownLandProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
