package com.benbaba.dadpat.host.utils;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.benbaba.dadpat.host.R;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;


public class AgentWebManager {
    private AgentWeb mAgentWeb;

    public AgentWebManager() {

    }

    /**
     * 打开一个Web界面
     */
    public void loadWeb(Activity activity, String url, ViewGroup parent) {
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
            mAgentWeb = null;
        }
        mAgentWeb = AgentWeb.with(activity).setAgentWebParent(parent, new ViewGroup.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.DEFAULT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .createAgentWeb()
                .ready()
                .go(url);
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    /**
     * 恢复
     */
    public void resume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    /**
     * 销毁
     */
    public void destory() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    /**
     * 调用Js方法
     */
    public void callJavaToJsMethod() {

    }

    /**
     * 注册js调用Java得类
     */
    public void registerJsToJava() {

    }

}
