package com.handpay.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handpay.launch.hp.R;
import com.handpay.launch.util.LogT;
import com.handpay.launch.util.RxToast;
import com.handpay.utils.CommonUtils;
import com.handpay.view.ActionBar;
import com.handpay.view.MyGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity implements FinalAdapter.OnAdapterListener {

    private MyGridView mGridView;
    private Activity activity = SettingsActivity.this;
    private List<Integer> ImgRES = new ArrayList<>();
    private List<String> homeTagList = new ArrayList<>();
    public static List<Integer> tvColor = new ArrayList<>();

    private int[] img = {R.drawable.setwifi, R.drawable.setxianshi, R.drawable.setlanya, R.drawable.setyidong,
            R.drawable.setvpn, R.drawable.setsave, R.drawable.setliuliang, R.drawable.setvoice,
            R.drawable.setbanben, R.drawable.set_download, R.drawable.set_sort, R.drawable.set_seticon, R.drawable.set_exit
    };
    public static String[] homeTag = {"WIFI", "显示", "蓝牙", "移动网络", "VPN", "设备信息"
            , "流量查看", "声音", "版本检测", "下载进度", "实时网速", "系统设置", "退出"
    };
    public static int[] mTvColor = {R.color.color_wifi, R.color.color_xianshi, R.color.color_lanya, R.color.color_yidongwangluo, R.color.color_vpn,
            R.color.color_cunchuxinxi, R.color.color_liuliangchakan, R.color.color_sound, R.color.color_banben, R.color.color_download,
            R.color.color_sort, R.color.color_system_setting, R.color.color_quite_app
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.settings_layout);
        super.onCreate(savedInstanceState);

        initGridView();
    }

    public ActionBar getActivityActionBar() {
        return new ActionBar("设置", true);
    }

    @Override
    public void bindView(FinalAdapter.FinalViewHolder finalViewHolder, Object content, String tag, Object color) {//
        ImageView imageView = (ImageView) finalViewHolder.getViewById(R.id.gridview_image);
        TextView title = (TextView) finalViewHolder.getViewById(R.id.gridview_title);
        imageView.setImageResource(((Integer) content));
        title.setText(tag);
        title.setTextColor(getResources().getColor((Integer) color));
//        title.setTextColor(getResources().getColor(SettingsIcon.getTextColor(tag)));
    }

    private void initGridView() {
        mGridView = (MyGridView) findViewById(R.id.my_gridview);
        ImgRES.clear();
        homeTagList.clear();
        tvColor.clear();
        for (int i = 0; i < img.length; i++) {
            ImgRES.add(img[i]);
            homeTagList.add(homeTag[i]);
            tvColor.add(mTvColor[i]);
        }
        LogT.w(tvColor.toString());
        mGridView.setAdapter(new FinalAdapter<Integer>(ImgRES, homeTagList, R.layout.item_gridview, this, tvColor));//
        mGridView.setNumColumns(3);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://wifi
                        CommonUtils.setWifi(activity);
                        // 追加一个界面切换动画
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
                        break;
                    case 1://显示
                        CommonUtils.setDisplay(activity);
                        // 追加一个界面切换动画
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
                        break;
                    case 2://蓝牙
                        CommonUtils.setBlueTooth(activity);
                        // 追加一个界面切换动画
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
                        break;
                    case 3://移动网络
                        CommonUtils.setNetworkMethod(activity);
                        // 追加一个界面切换动画
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
                        break;
                    case 4://vpn
                        CommonUtils.setVPN(activity);
                        // 追加一个界面切换动画
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果

                        break;
                    case 5://存储信息
                        // TODO: 2018/3/15 加上遮盖后被遮盖的数据不能更新
//                        startActivity(new Intent(
//                                android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
                        startActivity(new Intent(SettingsActivity.this, DeviceInfoActivity.class));

                        break;
                    case 6://流量查看

                        break;
                    case 7://声音
                        CommonUtils.setSound(activity);
                        // 追加一个界面切换动画
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果

                        break;
                    case 8://版本检测

                        break;
                    case 9://下载进度

                        break;
                    case 10://网速
                        startActivity(new Intent(SettingsActivity.this,NetSpeedActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
                        break;
                    case 11://系统设置

                        break;
                    case 12://退出

                        break;
                }
            }
        });
    }

    private void showEnvironment(File path) {
        LogT.w("AbsolutePath=" + path.getAbsolutePath() + " Path="
                + path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long avaiableBlocks = stat.getAvailableBlocks();
        long avaiableSize = avaiableBlocks * blockSize;
        long blockCount = stat.getBlockCount();
        long blockAllSize = blockCount * blockSize;
        LogT.w("avaiableSize=" + (avaiableSize >> 20) + "M");
        LogT.w("blockAllSize=" + (blockAllSize >> 20) + "M");
        String cardInfo = getResources().getString(R.string.phone_the_total_memory) + ":" + convertFileSize(blockAllSize) + ", " +
                getResources().getString(R.string.the_available_memory) + ":" + convertFileSize(avaiableSize);//1210M/1467M,1210M/1467M
        RxToast.info(activity, cardInfo);
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.2f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.2f KB", f);
        } else
            return String.format("%d B", size);
    }
}
