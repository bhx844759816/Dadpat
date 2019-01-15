package com.benbaba.dadpat.niosocketlib;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * UdpSocket得管理类
 */
public class UdpSocketManager {

    private static UdpSocketManager mInstance;
    private DatagramSocket mSocket;
    private boolean isReceiving; // 是否接收广播消息
    private byte[] mReceiveData = new byte[1024];
    private UdpThread mThread;
    private OnUdpReceiveCallBack mCallBack;
    private int mLocalPort;

    /**
     * 获取对象
     *
     * @param localPort
     * @return
     */
    public static UdpSocketManager getInstance() {
        if (mInstance == null) {
            synchronized (UdpSocketManager.class) {
                if (mInstance == null)
                    mInstance = new UdpSocketManager();
            }
        }
        return mInstance;
    }

    /**
     * 开启接收消息得线程
     */
    public void startReceiveThread(int localPort) {
        this.mLocalPort = localPort;
        isReceiving = true;
        if (mThread == null || !mThread.isAlive()) {
            mThread = new UdpThread();
            mThread.start();
        } else {
            mThread.notify();
        }
    }

    /**
     * 停止接收消息
     */
    public void release() {
        isReceiving = false;
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }

    /**
     * 设置接收线程得回调函数
     */
    public void setOnReceiveUdpMsgCallBack(OnUdpReceiveCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * 发送广播消息
     *
     * @param msg
     * @param serverPort
     */
    public void sendMsg(String msg, int serverPort) {
        sendMsg(msg, "255.255.255.255", serverPort);
    }

    /**
     * 发送单播消息
     *
     * @param msg 发送的消息内容
     * @param serverIp 服务器Ip地址
     * @param serverPort 服务器端口
     */
    public void sendMsg(String msg, String serverIp, int serverPort) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            byte[] sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, 0, sendData.length);
            sendPacket.setAddress(InetAddress.getByName(serverIp));
            sendPacket.setPort(serverPort);
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    /**
     * Udp接收线程
     */
    private class UdpThread extends Thread {

        @Override
        public void run() {
            if (mSocket == null || mSocket.isClosed()) {
                try {
                    mSocket = new DatagramSocket(new InetSocketAddress(mLocalPort));
                    mSocket.setBroadcast(true);
                    mSocket.setReuseAddress(true);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            DatagramPacket packet = new DatagramPacket(mReceiveData, 0, mReceiveData.length);
            String receiveMsg;
            while (isReceiving && !isInterrupted() && mSocket != null && !mSocket.isClosed()) {
                try {
                    Log.i("TAG", "startReceive");
                    mSocket.receive(packet);
                    receiveMsg = new String(mReceiveData, 0, packet.getLength());
                    String address = packet.getAddress().toString().substring(1);
                    if (mCallBack != null) {
                        mCallBack.receiveMsg(receiveMsg, address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            super.run();
        }
    }
}
