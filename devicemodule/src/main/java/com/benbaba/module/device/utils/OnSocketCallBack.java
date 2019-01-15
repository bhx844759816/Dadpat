package com.benbaba.module.device.utils;

import com.benbaba.dadpat.niosocketlib.ReceiveType;
import com.benbaba.module.device.bean.DrumBean;

public interface OnSocketCallBack {

    void receiveMsg(DrumBean bean, ReceiveType type);
}
