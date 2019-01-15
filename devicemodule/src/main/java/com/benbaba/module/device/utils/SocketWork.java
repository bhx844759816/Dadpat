package com.benbaba.module.device.utils;

import android.text.TextUtils;
import android.util.Log;

import com.benbaba.dadpat.niosocketlib.OnXSocketCallImpl;
import com.benbaba.dadpat.niosocketlib.ReceiveType;
import com.benbaba.dadpat.niosocketlib.XSocket;
import com.benbaba.module.device.bean.DrumBean;
import com.google.gson.Gson;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SocketWork {
    private static final int SEND_PORT = 10025;
    private static final int RECEIVE_PORT = 10026;
    private static final int TCP_SERVER_PORT = 10027;
    private static SocketWork mInstance;
    private String mServerIp;
    private Lock mLock;
    private Condition mCondition;
    private Gson mGson;
    private OnSocketCallBack mCallBack;

    public static SocketWork getInstance() {
        if (mInstance == null) {
            synchronized (SocketWork.class) {
                if (mInstance == null)
                    mInstance = new SocketWork();
            }
        }
        return mInstance;
    }

    /**
     * XSocket得回调
     */
    private OnXSocketCallImpl xSocketCall = new OnXSocketCallImpl() {
        @Override
        public void receiveMsg(String msg, String serverIp, ReceiveType type) {
            Log.i("TAG", "receiveMsg:" + msg);
            try {
                DrumBean receiveBean = mGson.fromJson(msg, DrumBean.class);
                if (receiveBean != null) {
                    mCallBack.receiveMsg(receiveBean, type);
                    switch (type) {
                        case RECEIVE_TYPE_UDP:
                            mServerIp = serverIp;
                            XSocket.init().getXSocketConfig().setServerIp(serverIp);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void connectTcpSuccess() {

        }

        @Override
        public void connectTcpError() {

        }

        @Override
        public void disConnectTcp() {

        }
    };


    private SocketWork() {
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mGson = new Gson();
        XSocket.init().getXSocketConfig()
                .setSendPort(SEND_PORT)
                .setReceivePort(RECEIVE_PORT)
                .setServerPort(TCP_SERVER_PORT)
                .setMaxConnectCount(3)
                .setConnectTimeOut(1000L);
        //设置回调
        XSocket.init().setOnXSocketCallBack(xSocketCall);
        //启动UdpReceive
        XSocket.init().startUdpReceive();
    }

    /**
     * 发送UDP消息
     *
     * @param msg 消息实体
     */
    public void sendUdpMsg(String msg) {
        XSocket.init().sendMsgByUdp(msg);
    }

    /**
     * 发送udp消息
     *
     * @param msg
     * @param ip
     */
    public void sendUdpMsg(String msg, String ip) {
        XSocket.init().sendMsgByUdp(ip, msg);
    }

    /**
     * 发送消息通过ip
     */
    public void sendUdpMsgByIp(String msg) {
        if (!TextUtils.isEmpty(mServerIp)) {
            XSocket.init().sendMsgByUdp(mServerIp, msg);
        }
    }

    public void setOnSocketCallBack(OnSocketCallBack callBack) {
        this.mCallBack = callBack;
    }

    /**
     * 判断是否TCP
     * @return
     */
    public boolean isTcpConnect() {
        return XSocket.init().isTcpConnect();
    }
}
