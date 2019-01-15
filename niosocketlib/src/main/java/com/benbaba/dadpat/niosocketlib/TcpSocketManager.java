package com.benbaba.dadpat.niosocketlib;

import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * TcpSocket得管理类
 */
public class TcpSocketManager {
    private static final int MAX_CONNECT_COUNTS = 5;
    private static final int CONNECT_TIMEOUT = 2000;
    private static TcpSocketManager mInstance;
    private IoConnector mConnector;
    private IoSession mSession;
    private OnTcpSocketCallBack mCallBack;

    /**
     * 1.
     * 2.
     * 3.
     * 4.
     * 5.
     */

    public static TcpSocketManager getInstance() {
        if (mInstance == null) {
            synchronized (TcpSocketManager.class) {
                if (mInstance == null)
                    mInstance = new TcpSocketManager();
            }
        }
        return mInstance;
    }

    /**
     * 构造函数
     */
    private TcpSocketManager() {
        init();
    }

    /**
     * 初始化连接器
     */
    private void init() {
        mConnector = new NioSocketConnector();
        mConnector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        mConnector.getFilterChain().addLast(
                "codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
                        LineDelimiter.WINDOWS.getValue())));
        mConnector.setHandler(new MyIoHandler());
    }

    /**
     * 设置消息回调
     */
    public void setOnTcpSocketCallBack(OnTcpSocketCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * 连接Tcp服务器 总共连接
     */
    public void connectServer(final String serverIp, final int serverPort) {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (count < MAX_CONNECT_COUNTS) {
                    try {
                        ConnectFuture future = mConnector.connect(new InetSocketAddress(serverIp, serverPort));
                        future.awaitUninterruptibly();// 等待连接创建完成
                        mSession = future.getSession();//获得session
                        break;
                    } catch (Exception e) {
                        count++;
                        e.printStackTrace();
                    }
                }
                if (mSession == null && mCallBack != null) {
                    mCallBack.connectError();
                } else if (mCallBack != null) {
                    mCallBack.connectSuccess();
                }
                super.run();
            }
        }.start();
    }

    /**
     * 关闭连接 释放资源
     */
    public void release() {
        if (mSession != null) {
            mSession.close(true);
            mSession = null;
        }
        if (mConnector != null) {
            mConnector.dispose();
            mConnector = null;
        }
    }

    /**
     * 向服务器发送消息
     *
     * @param msg
     */
    public boolean sendMsg(String msg) {
        if (mSession != null && !mSession.isClosing() && mSession.isConnected()) {
            mSession.write(msg);
        }
        if (mSession == null || !mSession.isConnected()) {
            return false;
        }
        WriteFuture writeFuture = mSession.write(msg);
        if (writeFuture == null) {
            return false;
        }
        writeFuture.awaitUninterruptibly();
        if (writeFuture.isWritten()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 连接 消息处理类
     */
    private class MyIoHandler extends IoHandlerAdapter {
        /**
         * 异常被捕获得时候
         *
         * @param session
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            cause.printStackTrace();
        }

        /**
         * 接收到消息得时候
         *
         * @param session
         * @param message
         * @throws Exception
         */
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Log.i("TAG", "messageReceived:" + message);
            if (mCallBack != null) {
                mCallBack.receiveMsg((String) message);
            }
        }

        /**
         * 连接通道空闲得时候
         *
         * @param session
         * @param status
         * @throws Exception
         */
        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            Log.i("TAG", "sessionIdle");
        }

        /**
         * 连接通道被关闭得时候
         *
         * @param session
         * @throws Exception
         */
        @Override
        public void sessionClosed(IoSession session) throws Exception {
            Log.i("TAG", "sessionClosed");
            if (mCallBack != null) {
                mCallBack.disConnectServer();
            }
        }
    }



}
