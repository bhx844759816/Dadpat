package com.benbaba.dadpat.host.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benbaba.dadpat.host.Constants;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.base.BaseAdapter;
import com.benbaba.dadpat.host.base.BaseViewHolder;
import com.benbaba.dadpat.host.bean.ItemBean;
import com.benbaba.dadpat.host.bean.PluginBean;
import com.benbaba.dadpat.host.utils.BitmapUtils;
import com.benbaba.dadpat.host.utils.PluginManager;
import com.benbaba.dadpat.host.view.WaveDrawable;

import java.util.List;

public class MainAdapter extends BaseAdapter<PluginBean> {

    public MainAdapter(Context context, List<PluginBean> data) {
        super(context, data, R.layout.adapter_main3);
    }

    @Override
    public void convert(BaseViewHolder holder, int pos, PluginBean pluginBean) {
        ImageView name = holder.getView(R.id.id_adapter_item_name);
        ImageView img = holder.getView(R.id.id_adapter_item_img);
        ImageView downland = holder.getView(R.id.id_adapter_item_downland);
        if ("2".equals(pluginBean.getIsRelease())) {
            name.setBackgroundResource(R.drawable.main_coming_soon);
            downland.setVisibility(View.GONE);
            img.setImageResource(pluginBean.getImgRes());
        } else {
            name.setBackgroundResource(Constants.RES_TEXT_MAP.get(pluginBean.getPluginName()));
            pluginBean.setWaveDrawable(getWaveDrawable(pluginBean.getImgRes()));
            if (pluginBean.isInstall()) {
                if (pluginBean.isNeedUpdate()) {
                    if(pluginBean.isDownLanding()){
                        downland.setVisibility(View.GONE);
                        img.setImageDrawable(pluginBean.getWaveDrawable());
                    }else {
                        downland.setVisibility(View.VISIBLE);
                        img.setImageDrawable(null);
                        img.setImageResource(pluginBean.getImgRes());
                    }
                } else {
                    downland.setVisibility(View.GONE);
                    img.setImageDrawable(null);
                    img.setImageResource(pluginBean.getImgRes());
                }
            } else {
                downland.setVisibility(View.GONE);
                img.setImageDrawable(pluginBean.getWaveDrawable());
            }
            if (!pluginBean.isInstall() || pluginBean.isNeedUpdate()) {
                pluginBean.getWaveDrawable().setLevel((int) (PluginManager.getInstance().getCurrentDownProgress(pluginBean.getUrl()) * 10000));
            }
        }
    }

    /**
     * 实例化下载得Drawable
     *
     * @return
     */
    private WaveDrawable getWaveDrawable(int res) {
        WaveDrawable waveDrawable = new WaveDrawable(mContext, res);
        waveDrawable.setIndeterminate(false);
        waveDrawable.setWaveSpeed(4);
        waveDrawable.setWaveAmplitude(10);
        return waveDrawable;
    }
}
