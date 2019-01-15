package com.benbaba.dadpat.niosocketlib;

public interface OnTcpSocketCallBack {
    /**
     * 连接服务器成功
     */
    void connectSuccess();

    /**
     * 连接服务器失败
     */
    void connectError();

    /**
     * 与服务器断开连接
     */
    void disConnectServer();

    /**
     * 接收消息
     */
    void receiveMsg(String msg);
}
