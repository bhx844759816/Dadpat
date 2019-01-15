package com.benbaba.module.device.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.benbaba.module.device.R;

public class ModifyNameDialog extends DialogFragment {
    private static final String FRAGMENT_TAG = "modify_name_dialog";
    private Context mContext;
    private EditText mEditText;
    private OnModifyDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnModifyDialogListener) {
            mListener = (OnModifyDialogListener) context;
        }
    }

    /**
     * 展示Dialog
     *
     * @param activity
     */
    public static void showDialog(FragmentActivity activity) {
        FragmentManager mManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = mManager.beginTransaction();
        ModifyNameDialog dialog = (ModifyNameDialog) mManager.findFragmentByTag(FRAGMENT_TAG);
        if (dialog == null) {
            dialog = new ModifyNameDialog();
        }
        if (!dialog.isVisible() || !dialog.isAdded()) {
            transaction.add(dialog, FRAGMENT_TAG);
        }
        transaction.commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_modify_device_name, container, false);
        mEditText = view.findViewById(R.id.id_modify_device_name_et);
        view.findViewById(R.id.id_modify_device_name_send)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissAllowingStateLoss();
                        mListener.sendDeviceName(mEditText.getText().toString().trim());
                    }
                });
        return view;
    }


    public interface OnModifyDialogListener {
        void sendDeviceName(String name);
    }
}
