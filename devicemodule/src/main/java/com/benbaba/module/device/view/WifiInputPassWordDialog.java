package com.benbaba.module.device.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.benbaba.module.device.R;
import com.benbaba.module.device.utils.OnSendWifiListener;

/**
 * 输入Wifi密码得Dialog
 */
public class WifiInputPassWordDialog extends DialogFragment implements View.OnClickListener {
    public static final String FRAGMENT_TAG = "wifi_input_dialog";
    private TextView mWifiNameTv;
    private EditText mPassWordEt;
    private String mWifiName;
    private Context mContext;
    private OnSendWifiListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSendWifiListener) {
            mListener = (OnSendWifiListener) context;
        }
        mContext = context;
    }

    /**
     * 展示Dialog
     *
     * @param activity
     */
    public static WifiInputPassWordDialog showDialog(FragmentActivity activity) {
        FragmentManager mManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = mManager.beginTransaction();
        WifiInputPassWordDialog dialog = (WifiInputPassWordDialog) mManager.findFragmentByTag(FRAGMENT_TAG);
        if (dialog == null) {
            dialog = new WifiInputPassWordDialog();
        }
        if (!dialog.isVisible() || !dialog.isAdded()) {
            transaction.add(dialog, FRAGMENT_TAG);
        }
        transaction.commit();
        return dialog;
    }

    public static void dismissDialog(FragmentActivity activity) {
        FragmentManager mManager = activity.getSupportFragmentManager();
        WifiInputPassWordDialog dialog = (WifiInputPassWordDialog) mManager.findFragmentByTag(FRAGMENT_TAG);
        if (dialog != null && dialog.isAdded() && dialog.isVisible()) {
            dialog.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_wifi_input_password, container, false);
        mWifiNameTv = view.findViewById(R.id.id_input_wifi_psd_name);
        mPassWordEt = view.findViewById(R.id.id_input_wifi_psd_et);
        view.findViewById(R.id.id_input_wifi_psd_confirm).setOnClickListener(this);
        view.findViewById(R.id.id_input_wifi_psd_cancel).setOnClickListener(this);
        mWifiNameTv.setText(mWifiName);
        return view;
    }

    public void setWifiName(String wifiName) {
        this.mWifiName = wifiName;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.id_input_wifi_psd_confirm) {
            mListener.sendWifi(mWifiName, mPassWordEt.getText().toString().trim());
            dismiss();
        } else if (i == R.id.id_input_wifi_psd_cancel) {
            dismiss();

        }
    }
}
