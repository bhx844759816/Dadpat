package com.benbaba.dadpat.host.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benbaba.dadpat.host.AppManager;
import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.adapter.MainAdapter;
import com.benbaba.dadpat.host.base.BaseActivity;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.bean.User;
import com.benbaba.dadpat.host.callback.OnAppUpdateDialogCallBack;
import com.benbaba.dadpat.host.callback.OnBlueToothSettingCallBack;
import com.benbaba.dadpat.host.callback.OnBufferDialogCallBack;
import com.benbaba.dadpat.host.callback.OnItemDragListener;
import com.benbaba.dadpat.host.callback.OnNetWorkRemindCallBack;
import com.benbaba.dadpat.host.callback.OnPerInfoDialogCallBack;
import com.benbaba.dadpat.host.callback.OnPromptDialogCallBack;
import com.benbaba.dadpat.host.callback.OnReStartAppDialogCallBack;
import com.benbaba.dadpat.host.callback.OnRecyclerItemClickListener;
import com.benbaba.dadpat.host.dialog.AppUpdateDialogFragment;
import com.benbaba.dadpat.host.dialog.LoadingDialogFragment;
import com.benbaba.dadpat.host.dialog.MessageDialogFragment;
import com.benbaba.dadpat.host.dialog.NetWorkDialogFragment;
import com.benbaba.dadpat.host.dialog.PerInfoDialogFragment;
import com.benbaba.dadpat.host.dialog.PromptDialogFragment;
import com.benbaba.dadpat.host.dialog.factory.DialogFactory;
import com.benbaba.dadpat.host.http.DefaultObserver;
import com.benbaba.dadpat.host.http.HttpManager;
import com.benbaba.dadpat.host.service.AppUpdateService2;
import com.benbaba.dadpat.host.utils.BlueToothManager;
import com.benbaba.dadpat.host.utils.BlueToothManager2;
import com.benbaba.dadpat.host.utils.DragItemCallBack;
import com.benbaba.dadpat.host.utils.FileUtils;
import com.benbaba.dadpat.host.utils.GridSpaceItemDecoration;
import com.benbaba.dadpat.host.utils.L;
import com.benbaba.dadpat.host.utils.MatisseUtils;
import com.benbaba.dadpat.host.utils.NetUtils;
import com.benbaba.dadpat.host.utils.PhoneUtils;
import com.benbaba.dadpat.host.utils.PluginManager;
import com.benbaba.dadpat.host.utils.SPUtils;
import com.benbaba.dadpat.host.utils.ShakeViewAnim;
import com.benbaba.dadpat.host.utils.ToastUtils;
import com.benbaba.dadpat.host.utils.WifiConnect;
import com.benbaba.dadpat.host.view.BottomLinearShaderView;
import com.benbaba.dadpat.host.view.NoScrollLayoutManager;
import com.benbaba.module.device.ui.DeviceListActivity2;
import com.bumptech.glide.Glide;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.compress.CompressHelper;
import com.zhihu.matisse.compress.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("checkresult")
public class MainActivity extends BaseActivity implements
        OnPromptDialogCallBack, OnPerInfoDialogCallBack, OnBufferDialogCallBack,
        OnNetWorkRemindCallBack, OnReStartAppDialogCallBack, OnAppUpdateDialogCallBack, OnBlueToothSettingCallBack {
    private static final int TAKE_PHOTO = 0x001;
    private static final int INSTALL_APK = 0x002;
    private static final int REQUEST_GPS = 0x003;
    private static final int REQUEST_ENABLE_BT = 0x004;
    @BindView(R.id.id_main_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.id_main_per_photo)
    CircleImageView mMainPerPhoto;
    @BindView(R.id.id_main_trash)
    ImageView mTrash;
    @BindView(R.id.id_main_per_name)
    TextView mPerName;
    @BindView(R.id.id_main_bottomShaderView)
    BottomLinearShaderView mShaderView;
    @BindView(R.id.id_main_blueTooth_setting)
    TextView mBlueToothSetting;
    private List<PluginBean> mPluginBeanList;
    private MainAdapter mMainAdapter;
    private User mUser;
    private ItemTouchHelper mHelper;
    private int mPos;
    private int mDownPos;
    private boolean isDeleteItemDialogShow;
    private View mDragView;
    private boolean isAllowNetWorkDownLand;// 是否允许
    private PerInfoDialogFragment mPerInfoDialog;
    private File mPhotoFile;
    private PluginBean mAppBean;
    private BlueToothManager mBlueToothManager;
    private BlueToothManager2 mBlueToothManager2;
    private WifiConnect mWifiConnect;

    /**
     * 4G和wifi切换得时候暂停和开始下载
     */
    private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            if (PluginManager.getInstance().isHaveTaskRunning()) {
                                PluginManager.getInstance().startAllDownLand();
                                DialogFactory.dismissNetWorkRemindDialog(MainActivity.this);
                            }
                        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            if (!isAllowNetWorkDownLand && PluginManager.getInstance().isHaveTaskRunning()) {
                                PluginManager.getInstance().stopAllDownLand();
                                NetWorkDialogFragment fragment = DialogFactory.showNetWorkRemindDialog(MainActivity.this);
                                fragment.setData(FileUtils.formatFileSize(PluginManager.getInstance().getNeedDownSize()));
                            }
                        }
                    }
                }
            }
        }
    };
    /**
     *
     * */
    private PluginManager.OnPluginManagerCallBack mPluginManagerCallBack = new PluginManager.OnPluginManagerCallBack() {
        @Override
        public void installFinish(PluginBean bean) {
            bean.setDownLanding(false);
            if (bean.isNeedUpdate()) {
                int localVersion = RePlugin.getPluginVersion(bean.getPluginName());
                boolean needUpdate = bean.getVersion() > localVersion;
                if (needUpdate) {
                    toast("更新完成");
                }
                bean.setNeedUpdate(needUpdate);
            } else {
                bean.setInstall(true);
            }
            sortPluginList(mPluginBeanList);
            mMainAdapter.notifyDataSetChanged();
        }

        @Override
        public void installError(PluginBean bean) {
            bean.setDownLanding(false);
            toast(bean.getPluginAlias() + "下载文件出错,请重新下载");
            mMainAdapter.notifyItemChanged(mPluginBeanList.indexOf(bean), bean);
            PluginManager.getInstance().cancelDownLand(bean.getUrl());

        }

        @Override
        public void downLandError(PluginBean bean) {
            bean.setDownLanding(false);
            toast("下载文件失败，请重新下载");
            mMainAdapter.notifyItemChanged(mPluginBeanList.indexOf(bean), bean);
            PluginManager.getInstance().cancelDownLand(bean.getUrl());
        }
    };

    /**
     * wifi连接帮助类
     */
    private WifiConnect.WifiConnectListener mConnectListener = isConnected -> runOnUiThread(() -> {
        if (isConnected) {
            ToastUtils.showShortToast(MainActivity.this, "连接设备wifi成功");
            checkScanBlueToothPermission();
        } else {
            ToastUtils.showShortToast(MainActivity.this, "连接设备wifi失败");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mUser = (User) getIntent().getSerializableExtra("User");
        initView();
        initData();
//        Configuration config = getResources().getConfiguration();
//        int smallestScreenWidth = config.smallestScreenWidthDp;
//        L.i("smallest width : " + smallestScreenWidth);
//        checkAppVersion(false);
        getPluginList();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        String url = "";
        if (mUser != null) {
            if (!TextUtils.isEmpty(mUser.getHeadImg()) && mUser.getHeadImg().startsWith("http")) {
                url = mUser.getHeadImg();
            } else if (!TextUtils.isEmpty(mUser.getHeadImg())) {
                url = HttpManager.BASE_URL + "/" + mUser.getHeadImg();
            }
            mPerName.setText(mUser.getUserName());
        }
        Glide.with(this)
                .load(url)
                .dontAnimate()
                .placeholder(R.drawable.per_touxiang)
                .error(R.drawable.per_touxiang)
                .into(mMainPerPhoto);

    }

    /**
     * 初始化View
     */
    private void initView() {
        mPluginBeanList = new ArrayList<>();
        PluginManager.getInstance().setOnPluginManagerCallBack(mPluginManagerCallBack);
        mMainAdapter = new MainAdapter(this, mPluginBeanList);
        mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(this));
        final NoScrollLayoutManager manager = new NoScrollLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        DragItemCallBack dragItemCallBack = new DragItemCallBack(mMainAdapter, mPluginBeanList, mTrash);
        dragItemCallBack.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void startDrag() {
                isDeleteItemDialogShow = false;
                setTrashVisible();
            }

            @Override
            public void deleteItem(View view, int position) {
                mDragView = view;
                mPos = position;
                isDeleteItemDialogShow = true;
                PromptDialogFragment fragment = DialogFactory.showPromptDialog(MainActivity.this);
                fragment.setPluginName(mPluginBeanList.get(position).getPluginAlias());
            }

            @Override
            public void clearView() {
                if (!isDeleteItemDialogShow) {
                    setTrashInVisible();
                }
            }
        });
        mHelper = new ItemTouchHelper(dragItemCallBack);
        mHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                List<PluginBean> list = mPluginBeanList.subList(mPluginBeanList.size() - 4, mPluginBeanList.size());
                mShaderView.setShaderView(list);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                List<PluginBean> list = mPluginBeanList.subList(mPluginBeanList.size() - 4, mPluginBeanList.size());
                mShaderView.setShaderView(list);
            }

        });
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int pos = vh.getAdapterPosition();
                PluginBean bean = mPluginBeanList.get(pos);
                if (bean.isInstall() && !bean.isNeedUpdate()) {
                    // 已安装且是最新得版本
                    startPluginActivity(mPluginBeanList.get(pos));
                } else {
                    // isRelease 为1得时候未发布 2得时候已发布
                    if (bean.getIsRelease().equals("2")) {
                        return;
                    }
                    //未下载 点击弹出下载确认对话框  已下载 点击暂停
                    if (PluginManager.getInstance().isTaskRunning(bean)) {
                        toast(bean.getPluginAlias() + "暂停下载");
                        PluginManager.getInstance().stopDownLand(mPluginBeanList.get(pos));
                    } else {
                        if (!NetUtils.isNetworkConnected(MainActivity.this)) {
                            toast("请检查网络连接");
                            return;
                        }
                        if (NetUtils.isWifiConnected(MainActivity.this) || isAllowNetWorkDownLand) {
                            toast(bean.getPluginAlias() + "开始下载");
                            bean.setDownLanding(true);
                            mMainAdapter.notifyItemChanged(pos);
                            PluginManager.getInstance().startDownLand(mPluginBeanList.get(pos));
                        } else {
                            mDownPos = pos;
                            NetWorkDialogFragment fragment = DialogFactory.showNetWorkRemindDialog(MainActivity.this);
                            fragment.setData(FileUtils.formatFileSize(Long.valueOf(bean.getApkSize())));
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                PluginBean pluginBean = mPluginBeanList.get(vh.getAdapterPosition());
                if (pluginBean.isInstall()) {
                    mHelper.startDrag(vh);
                }
            }
        });
        //实例化蓝牙管理类
        mBlueToothManager = new BlueToothManager(this);
        mBlueToothManager2 = new BlueToothManager2(this);
        mWifiConnect = new WifiConnect(this, mConnectListener);

    }

    /**
     * 获取插件化列表
     */
    private void getPluginList() {
        HttpManager.getInstance().getPluginList()
                .map(list -> {
                    for (PluginBean bean : list) {
                        bean.setSavePath(Constants.PLUGIN_SAVE_DIR + String.valueOf(bean.getPluginName() + ".apk"));
                        if (bean.getIsRelease().equals("1")) { //当发布得时候才会去判断释放需要更新或者安装
                            boolean isInstall = RePlugin.isPluginInstalled(bean.getPluginName());
                            if (isInstall) {
                                int version = RePlugin.getPluginVersion(bean.getPluginName());
                                boolean needUpdate = bean.getVersion() > version;
                                bean.setNeedUpdate(needUpdate);
                            }
                            bean.setInstall(isInstall);
                        }
                    }
                    sortPluginList(list);
                    return list;
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable -> {
                    if (!disposable.isDisposed())
                        DialogFactory.showLoadingDialog(MainActivity.this);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(MainActivity.this))
                .subscribe(list -> {
                    mPluginBeanList.clear();
                    mPluginBeanList.addAll(list);
                    mMainAdapter.notifyDataSetChanged();
                }, throwable -> L.i("getPluginList：" + throwable.getLocalizedMessage()));

    }

    /**
     * 排序
     */
    private void sortPluginList(List<PluginBean> list) {
        //按照是否发布排序
        Collections.sort(list, (o1, o2) -> {
            int isRelease_o1 = Integer.valueOf(o1.getIsRelease());
            int isRelease_o2 = Integer.valueOf(o2.getIsRelease());
            return isRelease_o1 - isRelease_o2;
        });
        // 按照安装顺序排序
        Collections.sort(list, (o1, o2) -> {
            boolean isInstall_o1 = o1.isInstall();
            boolean isInstall_o2 = o2.isInstall();
            if (isInstall_o1 && !isInstall_o2) {
                return -1;
            } else if (!isInstall_o1 && isInstall_o2) {
                return 1;
            }
            return 0;
        });
        // 设置List显示得图片
        int index = 0;
        for (PluginBean pluginBean : list) {
            if (pluginBean.getIsRelease().equals("1")) {
                pluginBean.setImgRes(Constants.RES_IMG_MAP.get(pluginBean.getPluginName()));
            } else {
                switch (index) {
                    case 0:
                        pluginBean.setImgRes(R.drawable.main_item_bitmap_01);
                        break;
                    case 1:
                        pluginBean.setImgRes(R.drawable.main_item_bitmap_02);
                        break;
                    case 2:
                        pluginBean.setImgRes(R.drawable.main_item_bitmap_03);
                        break;
                    case 3:
                        pluginBean.setImgRes(R.drawable.main_item_bitmap_04);
                        break;
                }
                index++;
                if (index == 4) {
                    index = 0;
                }
            }
        }
    }

    /**
     * 跳转到插件的Activity
     *
     * @param bean
     */
    private void startPluginActivity(PluginBean bean) {
        if (!bean.isInstall() || bean.isNeedUpdate() || bean.getIsRelease().equals("2")) {
            return;
        }
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            PluginInfo info = RePlugin.getPluginInfo(bean.getPluginName());
            if (info != null) {
                boolean result = RePlugin.preload(info);
                e.onNext(result);
            } else {
                e.onNext(false);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    LoadingDialogFragment fragment = DialogFactory.showLoadingDialog(MainActivity.this);
                    String loadingGifName = "";
                    if ("Plugin_Web_Calendar".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_calendar.gif";
                    } else if ("Plugin_Web_Astronomy".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_astronomy.gif";
                    } else if ("Plugin_Web_ChinaHistory".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_china_history.gif";
                    } else if ("Plugin_Web_Earth".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_earth.gif";
                    } else if ("Plugin_Web_Animal".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_animal.gif";
                    } else if ("Plugin_Web_English".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_abc.gif";
                    } else if ("Plugin_Web_Picture".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_picture.gif";
                    } else if ("Plugin_Web_WorldHistory".equals(bean.getPluginName())) {
                        loadingGifName = "gif/loading_world_history.gif";
                    }
                    fragment.setLoadingGifName(loadingGifName);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Intent intent = RePlugin.createIntent(bean.getPluginName(), bean.getMainClass().trim());
                        boolean isStartResult = RePlugin.startActivity(MainActivity.this, intent);
                        if (!isStartResult) {
                            DialogFactory.showReStartAPPDialog(MainActivity.this);
                        }
                    }
                });
    }

    /**
     * 显示垃圾桶
     */
    private void setTrashVisible() {
        mTrash.setVisibility(View.VISIBLE);
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mTrash, View.TRANSLATION_X, 200, 0);
//        animator.setDuration(1000);
//        animator.start();
        mTrash.animate()
                .translationX(0)
                .setDuration(1000)
                .start();
    }

    /**
     * 隐藏垃圾桶
     */
    private void setTrashInVisible() {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mTrash, View.TRANSLATION_X, 0, 200);
//        animator.setDuration(1000);
//        animator.start();
        mTrash.animate()
                .translationX(200)
                .setDuration(1000)
                .start();
    }

    /**
     * 确定删除
     */
    @Override
    public void confirmDelete() {
        PluginManager.getInstance().unInstallPlugin(mPluginBeanList.get(mPos), MainActivity.this);
        PluginBean bean2 = mPluginBeanList.get(mPos);
        bean2.setInstall(false);
        sortPluginList(mPluginBeanList);
        mMainAdapter.notifyDataSetChanged();
        ObjectAnimator animator = ShakeViewAnim.tada(mTrash);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setTrashInVisible();
                super.onAnimationEnd(animation);
            }
        });
        animator.start();
        mDragView.setVisibility(View.VISIBLE);

    }

    /**
     *
     */
    @Override
    public void cancelDelete() {
        setTrashInVisible();
        mDragView.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.id_main_per_photo, R.id.id_main_notice, R.id.id_main_wifi_setting,
            R.id.id_main_blueTooth_setting, R.id.id_main_song_list_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_main_per_photo:
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("Plugin_CoCos", "org.cocos2dx.javascript.AppActivity"));
//                RePlugin.startActivity(MainActivity.this, intent);
//                Intent intent = new Intent(this, WebActivity.class);
//                startActivity(intent);
                mPerInfoDialog = DialogFactory.showPerInfoDialog(this);
                mPerInfoDialog.setData(mUser, mPhotoFile);
                break;
            case R.id.id_main_notice: {
                MessageDialogFragment fragment = DialogFactory.showMessageDialog(this);
                fragment.setState(MessageDialogFragment.STATE_NOTICE);
                break;
            }
            case R.id.id_main_wifi_setting:
                startActivity(new Intent(MainActivity.this, DeviceListActivity2.class));
                break;
            case R.id.id_main_blueTooth_setting://点击蓝牙按钮
                connectDeviceWifi();
                break;
            case R.id.id_main_song_list_setting://点击歌单按钮
                DialogFactory.showSongListDialog(MainActivity.this);
                break;
        }
    }

    /**
     * 检查蓝牙搜索得权限
     */
    private void checkScanBlueToothPermission() {
        new RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (locManager != null && !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                // 跳转到定位设置界面
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, REQUEST_GPS);
                            } else {
                                startScanBlueToothDevice();
                            }
                        }
                    }
                });
    }

    /**
     * 开始扫描
     */
    private void startScanBlueToothDevice() {
        if (mBlueToothManager2.isEnabled()) {
            mBlueToothManager2.startScan();
            DialogFactory.showBlueToothListDialog(MainActivity.this);
        } else {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//需要BLUETOOTH权限
//            startActivityForResult(intent, REQUEST_ENABLE_BT);
            DialogFactory.showBlueToothSettingDialog(MainActivity.this);

        }
    }

    /**
     * 停止扫描蓝牙
     */
    public void stopScanBlueToothDevice() {
        if (mBlueToothManager2 != null) {
            mBlueToothManager2.stopScan();
        }
    }

    /**
     * 断开设备连接
     */
    public void disConnectDeviceWifi() {
        if (mWifiConnect != null && Constants.DEVICE_WIFI_SSID.equals(mWifiConnect.getWifiSSID())) {
            mWifiConnect.disconnectWifi();
        }
    }


    /**
     * 连接设备wifi
     */
    private void connectDeviceWifi() {
        if (Constants.DEVICE_WIFI_SSID.equals(mWifiConnect.getWifiSSID())) {
            //发送
            checkScanBlueToothPermission();
        } else {
            ToastUtils.showShortToast(this, "开始连接设备wifi");
            mWifiConnect.connect(Constants.DEVICE_WIFI_SSID, Constants.DEVICE_WIFI_PASSWORD, WifiConnect.SecurityMode.WPA);
        }
    }

    /**
     * 获取蓝牙列表
     *
     * @return
     */
    public List<BluetoothDevice> getBlueToothDeviceList() {
        return mBlueToothManager2.getDeviceList();
    }

    @Override
    public void onBufferDialogDismiss() {

    }

    /**
     * 点击头像更改相片
     */
    @Override
    public void takePhoto() {
        new RxPermissions(this).requestEach(Manifest.permission.CAMERA)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(permission -> {
                    if (permission.granted) {
                        MatisseUtils.openPhoto(this, TAKE_PHOTO, 1);
                    } else if (!permission.shouldShowRequestPermissionRationale) {
                        DialogFactory.showPermissionRefuseDialog(MainActivity.this);
                    }
                });
    }

    /**
     * 保存用户信息
     *
     * @param userName
     * @param birthday
     * @param sex
     */
    @Override
    public void saveUserInfo(String userName, String birthday, int sex) {
        if (mPhotoFile == null) {
            saveUserInfoNoPhoto(userName, String.valueOf(sex), birthday);
        } else {
            saveUserInfoAndPhoto(userName, String.valueOf(sex), birthday);
        }
    }

    /**
     * 退出登陆
     */
    @Override
    public void loginOff() {
        HttpManager.getInstance().doLogout().subscribe(new DefaultObserver<>());
        SPUtils.put(getApplicationContext(), Constants.SP_LOGIN, false);
        SPUtils.put(getApplicationContext(), Constants.SP_TOKEN, "");
        startActivity(new Intent(this, LoginActivity.class));
        AppManager.getAppManager().finishAllActivity();
    }

    /**
     * 检查版本
     */
    @Override
    public void checkVersion() {
        checkAppVersion();
    }

    /**
     * 上传用户信息
     */
    public void saveUserInfoNoPhoto(String userName, String userGender, String userBirthday) {
        HttpManager.getInstance().updateUserInfo(mUser.getUserId(), userName, userGender, userBirthday)
                .flatMap(httpResult -> HttpManager.getInstance().getUser())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable -> DialogFactory.showLoadingDialog(MainActivity.this))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(MainActivity.this))
                .subscribe(user -> {
                    mUser = user;
                    initData();
                }, throwable -> L.i("saveUserInfoNoPhoto:" + throwable.getLocalizedMessage()));
    }

    /**
     * 上传用户信息以及图片
     */
    public void saveUserInfoAndPhoto(String userName, String userGender, String userBirthday) {
        HttpManager.getInstance().updateUserPhoto(mPhotoFile)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable -> DialogFactory.showLoadingDialog(MainActivity.this))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> DialogFactory.dismissLoadingDialog(MainActivity.this))
                .flatMap(headImgBean ->
                        HttpManager.getInstance().updateUserInfo(mUser.getUserId(), userName, userGender, userBirthday))
                .flatMap(httpResult ->
                        HttpManager.getInstance().getUser())
                .subscribe(user -> {
                    mUser = user;
                    initData();
                }, throwable -> L.i("saveUserInfoNoPhoto:" + throwable.getLocalizedMessage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.i("onActivityResult :INSTALL_APK:" + requestCode + "," + resultCode);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 原文件
                    File file = FileUtil.getFileByPath(Matisse.obtainPathResult(data).get(0));
                    // 压缩后的文件（多个文件压缩可以循环压缩）
                    mPhotoFile = CompressHelper.getDefault(getApplicationContext()).compressToFile(file);
                    Glide.with(this)
                            .load(mPhotoFile)
                            .dontAnimate()
                            .placeholder(R.drawable.per_touxiang)
                            .error(R.drawable.per_touxiang)
                            .into(mMainPerPhoto);
                    mPerInfoDialog.setPhotoFile(mPhotoFile);
                }
                break;
            case INSTALL_APK://安装
                L.i("onActivityResult :INSTALL_APK");
                if (Build.VERSION.SDK_INT >= 26) {
                    boolean b = getPackageManager().canRequestPackageInstalls();
                    if (b) {
                        startInstallAPP();
                    }
                }
                break;
            case REQUEST_ENABLE_BT: //蓝牙设置
                startScanBlueToothDevice();
                break;
            case REQUEST_GPS: //定位权限
                startScanBlueToothDevice();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DialogFactory.dismissLoadingDialog(this);
        mWifiConnect.registerReceiver();
        registerWifiChangeReceiver();
        PluginManager.getInstance().startAllDownLand();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetReceiver);
        mWifiConnect.unRegisterReceiver();
        PluginManager.getInstance().stopAllDownLand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PluginManager.getInstance().release();
    }

    /**
     * 注册wifi切换得监听广播
     */
    private void registerWifiChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, filter);
    }

    /**
     * 确定允许在4G网下载
     */
    @Override
    public void continueDownLand() {
        isAllowNetWorkDownLand = true;
        PluginBean bean = mPluginBeanList.get(mDownPos);
        PluginManager.getInstance().startDownLand(bean);
        bean.setDownLanding(true);
        mMainAdapter.notifyItemChanged(mDownPos);
    }

    /**
     * 确定重启App
     */
    @Override
    public void confirmRestartApp() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            System.exit(0);
        }
    }

    @Override
    public void cancelRestartApp() {
        DialogFactory.dismissLoadingDialog(MainActivity.this);
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            toast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            AppManager.getAppManager().AppExit(MainActivity.this);
        }
    }

    /**
     * 确定更新App
     */
    @Override
    public void confirmUpdateApp() {
        checkApkInstallPermission();
    }

    /**
     * 适配8.0手机得外部安装Apk得权限
     */
    private void checkApkInstallPermission() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (!b) {
                new RxPermissions(this).request(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .subscribe(aBoolean -> {
                            if (!aBoolean) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                startActivityForResult(intent, INSTALL_APK);
                            } else {
                                startInstallAPP();
                            }
                        });
            } else {
                startInstallAPP();
            }
        } else {
            startInstallAPP();
        }

    }

    /**
     * 开始打开后台下载APK
     */
    private void startInstallAPP() {
        Intent intent = new Intent(MainActivity.this, AppUpdateService2.class);
        intent.putExtra("AppBean", mAppBean);
        startService(intent);
    }

    /**
     * 获取APP得版本号
     */
    private void checkAppVersion() {
        //检查更新    //是否需要更新   //后台下载更新APP    //安装APP
        HttpManager.getInstance().getHostApp()
                .subscribe(list -> {
                            mAppBean = list.get(0);
                            int versionCode = PhoneUtils.getLocalVersion(MainActivity.this);
                            if (mAppBean.getVersion() > versionCode && mAppBean.getIsRelease().equals("1")) {
                                AppUpdateDialogFragment fragment = DialogFactory.showAppUpdateDialog(MainActivity.this);
                                fragment.setMessage(mAppBean.getVersionName(), FileUtils.formatFileSize(Long.valueOf(mAppBean.getApkSize())));
                            } else {
                                ToastUtils.showShortToast(MainActivity.this, "当前是最新版本");
                            }
                        },
                        throwable -> L.i("checkAppVersion：" + throwable.getLocalizedMessage()));
    }

    @Override
    public void confirmBlueToothSetting() {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }
}
