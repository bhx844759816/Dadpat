package com.benbaba.dadpat.host.utils;

import android.text.TextUtils;

import com.benbaba.dadpat.host.bean.drum.BlueToothBody;
import com.benbaba.dadpat.host.bean.drum.DefaultBody;
import com.benbaba.dadpat.host.bean.drum.DrumBean2;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("checkresult")
public class SocketManager {
    //
    private static final int SEND_PORT = 10025;
    private static final int RECEIVE_PORT = 10026;
    //接收的缓冲数据
    private OnSocketReceiveCallBack mCallBack;
    private DatagramSocket mSocket;
    private Disposable mSocketDisposable;
    private Gson mGson;
    private byte[] mReceiveData = new byte[2048];

    public SocketManager(OnSocketReceiveCallBack callBack) {
        mCallBack = callBack;
        mGson = new Gson();
    }

    /**
     * 开启接收Udp得线程
     */
    public void startReceiveUdpMsg() {
        mSocketDisposable = Observable.create((ObservableOnSubscribe<String>) e -> {
            L.i("startReceiveUdpMsg");
            try {
                if (mSocket == null || mSocket.isClosed()) {
                    mSocket = new DatagramSocket(new InetSocketAddress(RECEIVE_PORT));
                    mSocket.setBroadcast(true);
                    mSocket.setReuseAddress(true);
                }
                DatagramPacket receivePacket = new DatagramPacket(mReceiveData, mReceiveData.length);
                while (!e.isDisposed() && !Thread.interrupted()) {
                    mSocket.receive(receivePacket);
                    byte[] data = Arrays.copyOf(mReceiveData, receivePacket.getLength());
                    e.onNext(new String(data));
                }
            } catch (SocketException so) {
                so.printStackTrace();
            } finally {
                release();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg -> {
                    L.i("receive msg:" + msg);
                    parseMsg(msg);
                });
    }

    private void parseMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(msg);
            JSONObject header_obj = obj.getJSONObject("header");
            String action = header_obj.getString("action");
            DrumBean2 drumBean2;
            if (action.equals("getBluetooth")) {
                drumBean2 = mGson.fromJson(msg, new TypeToken<DrumBean2<BlueToothBody>>() {
                }.getType());
            } else {
                drumBean2 = mGson.fromJson(msg, new TypeToken<DrumBean2<DefaultBody>>() {
                }.getType());
            }
            if (mCallBack != null) {
                mCallBack.receiveMsg(drumBean2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 发送Udp消息
     */
    public void sendReceiveUdpMsg(String msg) {
//        if (TextUtils.isEmpty(getLocalIPAddress())) {
//            return;
//        }
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            L.i("sendReceiveUdpMsg:" + msg);
            L.i("getLocalIPAddress():" + getLocalIPAddress());
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            byte[] sendData = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(sendData, 0, sendData.length);
            packet.setAddress(InetAddress.getByName("255.255.255.255"));
            packet.setPort(SEND_PORT);
            socket.send(packet);
            socket.close();
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                }, throwable -> L.i("throwable:" + throwable.getLocalizedMessage()));

    }

    /**
     * 释放资源
     */
    public void release() {
        if (mSocketDisposable != null) {
            mSocketDisposable.dispose();
            mSocketDisposable = null;
        }
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
            mSocket = null;
        }
    }

    /**
     * Socket得回调
     */
    public interface OnSocketReceiveCallBack {
        void receiveMsg(DrumBean2 bean);
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    private static String getLocalIPAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }
}
