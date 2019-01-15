package com.benbaba.dadpat.host.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseDialogFragment;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.callback.OnPerInfoDialogCallBack;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.utils.AppCacheUtils;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人信息的Dialog
 */
public class PerInfoDialogFragment extends BaseDialogFragment {
    @BindView(R.id.id_per_head)
    CircleImageView mHead;
    @BindView(R.id.id_per_name)
    EditText mName;
    @BindView(R.id.id_per_birthday)
    EditText mBirthday;
    @BindView(R.id.id_per_girl)
    RadioButton mGirl;
    @BindView(R.id.id_per_boy)
    RadioButton mBoy;
    @BindView(R.id.id_per_account_img)
    ImageView mAccountImg;
    @BindView(R.id.id_per_total_cache)
    TextView mTotalCacheView;
    private User mUser;
    private DatePickerDialog mDataPickerDialog;
    private OnPerInfoDialogCallBack mCallBack;
    private File mPhotoFile;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof OnPerInfoDialogCallBack) {
            mCallBack = (OnPerInfoDialogCallBack) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCallBack != null) {
            mCallBack = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_person_message;
    }

    @Override
    public void initData() {
        String url = "";
        if (mUser != null) {
            String loginType = (String) SPUtils.get(mContext, Constants.SP_LOGIN_TYPE, "phone");
            if ("phone".equals(loginType)) {
                mAccountImg.setBackgroundResource(R.drawable.per_phone);
            } else if ("weixin".equals(loginType)) {
                mAccountImg.setBackgroundResource(R.drawable.per_wx);
            } else if ("weibo".equals(loginType)) {
                mAccountImg.setBackgroundResource(R.drawable.per_sina);
            }
            if (!TextUtils.isEmpty(mUser.getUserBirthday())) {
                mBirthday.setText(mUser.getUserBirthday());
            }
            String sex = mUser.getUserSex();
            if (TextUtils.isEmpty(sex) || sex.equals("0")) {
                mBoy.setChecked(false);
                mGirl.setChecked(false);
            } else if (sex.equals("1")) {
                mBoy.setChecked(true);
                mGirl.setChecked(false);
            } else if (sex.equals("2")) {
                mBoy.setChecked(false);
                mGirl.setChecked(true);
            }
            if (!TextUtils.isEmpty(mUser.getUserName())) {
                mName.setText(mUser.getUserName());
            }
            if (!TextUtils.isEmpty(mUser.getHeadImg()) && mUser.getHeadImg().startsWith("http")) {
                url = mUser.getHeadImg();
            } else {
                url = HttpManager.BASE_URL + "/" + mUser.getHeadImg();
            }
        }
        if (mPhotoFile == null) {
            Glide.with(mContext).load(url).dontAnimate().placeholder(R.drawable.per_touxiang).error(R.drawable.per_touxiang).into(mHead);
        } else {
            Glide.with(mContext).load(mPhotoFile).dontAnimate().placeholder(R.drawable.per_touxiang).error(R.drawable.per_touxiang).into(mHead);
        }
        try {
            mTotalCacheView.setText(AppCacheUtils.getTotalCacheSize(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示日期对话框
     */
    private void showDataPickerDialog() {
        if (mDataPickerDialog == null) {
            final Calendar ca = Calendar.getInstance();
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH);
            int day = ca.get(Calendar.DAY_OF_MONTH);
            mDataPickerDialog = new DatePickerDialog(mContext, mDateListener, year, month, day);
        }
        mDataPickerDialog.show();
    }

    /**
     * 设置图片显示
     *
     * @param file
     */
    public void setPhotoFile(File file) {
        mPhotoFile = file;
        Glide.with(mContext).load(mPhotoFile).error(R.drawable.per_touxiang).into(mHead);
    }

    /**
     * 日期对话框选择得回调
     */
    private DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            mBirthday.setText(time);
        }
    };

    /**
     * 设置用户对象
     *
     * @param user
     */
    public void setData(User user, File photoFile) {
        this.mUser = user;
        this.mPhotoFile = photoFile;
    }

    @OnClick({R.id.id_per_birthday, R.id.id_per_head, R.id.id_per_check_version, R.id.id_per_user_feedback,
            R.id.id_per_login_off, R.id.id_per_clear_cache, R.id.id_per_copyright,
            R.id.id_per_save, R.id.id_per_user_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_per_birthday:
                showDataPickerDialog();
                break;
            case R.id.id_per_head:
                if (mCallBack != null)
                    mCallBack.takePhoto();
                break;
            case R.id.id_per_user_protocol:
                DialogFactory.showUserProtocolDialog(getActivity());
                break;
            case R.id.id_per_login_off:
                dismiss();
                if (mCallBack != null)
                    mCallBack.loginOff();
                break;
            case R.id.id_per_save:
                String userName = mName.getText().toString().trim();
                String birthday = mBirthday.getText().toString().trim();
                int sex = 0;
                if (mGirl.isChecked()) {
                    sex = 2;
                }
                if (mBoy.isChecked()) {
                    sex = 1;
                }
                if (mCallBack != null)
                    mCallBack.saveUserInfo(userName, birthday, sex);
                dismiss();
                break;
            case R.id.id_per_check_version://检查版本
                if (mCallBack != null) {
                    mCallBack.checkVersion();
                }
                break;
            case R.id.id_per_user_feedback://反馈意见
                FeedBackDialogFragment fragment = DialogFactory.showFeedBackDialog(getActivity());
                fragment.setUser(mUser);
                break;
            case R.id.id_per_clear_cache://清理缓存
                AppCacheUtils.clearAllCache(getContext());
                mTotalCacheView.setText(AppCacheUtils.getTotalCacheSize(getContext()));
                break;
            case R.id.id_per_copyright://版权信息
                break;

        }
    }

}
