package com.benbaba.dadpat.host.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.callback.OnConnectDeviceCallBack;
import com.benbaba.dadpat.host.utils.DeviceUdpUtils;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.SocketManager;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.benbaba.dadpat.host.utils.WifiConnect;
import com.trello.rxlifecycle2.components.RxFragment;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 发送wifi密码给鼓
 */
public class ConnectDeviceFragment extends RxFragment {
    @BindView(R.id.id_view_connect_wifi_et)
    EditText mConnectWifiEt;
    @BindView(R.id.id_view_connect_wifi_psd)
    ImageView mConnectWifiPsd;
    private Context mContext;
    Unbinder unbinder;
    private OnConnectDeviceCallBack mCallBack;
    private boolean isOpenPsd;

    /**
     * 实例化fragment对象
     *
     * @return
     */
    public static ConnectDeviceFragment newInstance() {
        return new ConnectDeviceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnConnectDeviceCallBack) {
            mCallBack = (OnConnectDeviceCallBack) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_select_wifi_connect, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.id_view_connect_wifi_psd, R.id.id_view_connect_wifi_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_view_connect_wifi_psd:
                isOpenPsd = !isOpenPsd;
                if (isOpenPsd) {
                    mConnectWifiEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConnectWifiPsd.setBackgroundResource(R.drawable.login_eye_open);
                } else {
                    mConnectWifiEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConnectWifiPsd.setBackgroundResource(R.drawable.login_eye_close);
                }
                break;
            case R.id.id_view_connect_wifi_send:
                String psd = mConnectWifiEt.getText().toString().trim();
                if (!TextUtils.isEmpty(psd)) {
                    if (mCallBack != null)
                        mCallBack.connectWifi(psd);
                } else {
                    ToastUtils.showShortToast(mContext, "请输入wifi密码");
                }
                break;
        }
    }
}
