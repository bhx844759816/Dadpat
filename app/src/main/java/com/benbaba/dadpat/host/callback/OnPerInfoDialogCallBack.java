package com.benbaba.dadpat.host.callback;

import com.benbaba.dadpat.host.bean.User;

public interface OnPerInfoDialogCallBack {
    // 选择头像
    void takePhoto();

    // 保存用户信息 sex 0 女 1 男
    void saveUserInfo(String userName, String birthday, int sex);

    // 注销账户信息
    void loginOff();

    void checkVersion();
}
