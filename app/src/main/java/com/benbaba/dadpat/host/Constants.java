package com.benbaba.dadpat.host;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/12/18.
 */
public class Constants {
    public static final String PLUGIN_SAVE_DIR = Environment.getExternalStorageDirectory() + File.separator + "benbaba"
            + File.separator + "plugins" + File.separator;
    public static final String APP_SAVE_DIR = Environment.getExternalStorageDirectory() + File.separator + "benbaba"
            + File.separator + "apk";
    public static final String BASE_URL = "http://www.dadpat.com/";

    public static final String SP_LOGIN = "login";
    public static final String SP_LOGIN_TYPE = "login_type";
    public static final String SP_TOKEN = "token";
    public static final String DEVICE_WIFI_SSID = "dadpat";
    //    public static final String DEVICE_WIFI_SSID = "benbb";
    public static final String DEVICE_WIFI_PASSWORD = "dadpat@123";

    public static final HashMap<String, Integer> RES_IMG_MAP = new HashMap<String, Integer>() {
        {
            put("Plugin_Web_Calendar", R.drawable.main_item_calendar);
            put("Plugin_Web_Astronomy", R.drawable.main_item_astronomy);
            put("Plugin_Web_ChinaHistory", R.drawable.main_item_chinese_history);
            put("Plugin_Web_Earth", R.drawable.main_item_earth);
            put("Plugin_Web_Animal", R.drawable.main_item_animal);
            put("Plugin_Web_English", R.drawable.main_item_abc);
            put("Plugin_Web_Picture", R.drawable.main_item_picture);
            put("Plugin_Web_WorldHistory", R.drawable.main_item_world_history);
            put("Plugin_Dadpat", R.drawable.main_item_dadpat);
            put("Plugin_Rhythm", R.drawable.main_item_rhythm);
            put("Plugin_DadpatGuess", R.drawable.main_item_guess);
            put("Plugin_Piano", R.drawable.main_item_piano);
        }
    };
    public static final HashMap<String, Integer> RES_TEXT_MAP = new HashMap<String, Integer>() {
        {
            put("Plugin_Web_Calendar", R.drawable.main_item_calendar_text);
            put("Plugin_Web_Astronomy", R.drawable.main_item_astronomy_text);
            put("Plugin_Web_ChinaHistory", R.drawable.main_item_chinese_history_text);
            put("Plugin_Web_Earth", R.drawable.main_item_earth_text);
            put("Plugin_Web_Animal", R.drawable.main_item_animal_text);
            put("Plugin_Web_English", R.drawable.main_item_abc_text);
            put("Plugin_Web_Picture", R.drawable.main_item_picture_text);
            put("Plugin_Web_WorldHistory", R.drawable.main_item_world_history_text);
            put("Plugin_Dadpat", R.drawable.main_item_dadpat_text);
            put("Plugin_Rhythm", R.drawable.main_item_rhythm_text);
            put("Plugin_DadpatGuess", R.drawable.main_item_guess_text);
            put("Plugin_Piano", R.drawable.main_item_piano_text);
        }
    };
//    public static final String DEVICE_WIFI_PASSWORD = "benbaba@123/Z/X";

//    // web插件得别名
//    public static final String PLUGIN_WEB = "Plugin_Web";
//    public static final String PLUGIN_WEB_URL = "http://192.168.1.108:8000/Plugin_Web.apk";
//    // 节奏游戏插件得别名
//    public static final String PLUGIN_RHYTHM = "Plugin_Rhythm";
//    public static final String PLUGIN_RHYTHM_URL = "http://192.168.1.108:8000/Plugin_Rhythm.apk";
//    // 爸爸拍拍游戏插件得别名
//    public static final String PLUGIN_INSTRUMENT = "Plugin_Instrument";
//    public static final String PLUGIN_INSTRUMENT_URL = "http://192.168.1.108:8000/Plugin_Instrument.apk";
//    // 爸爸拍拍游戏插件得入口得activity得className
//    public static final String PLUGIN_INSTRUMENT_MAIN_CLASS_NAME = "com.benbaba.dadpat.plugin.game.instruments.ui.MainActivity";
//    public static final String PLUGIN_RHYTHM_MAIN_CLASS_NAME = "com.benbaba.dadpat.plugin.game.rhythm.ui.MainActivity";
//    public static final String PLUGIN_WEB_MAIN_CLASS_NAME = "com.benbaba.dadpat.plugin.game.web.MainActivity";
}
