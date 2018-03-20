package com.handpay.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handpay.launch.hp.R;
import com.handpay.launch.util.LogT;
import com.handpay.utils.CommonUtils;
import com.handpay.utils.taskscheduler.Task;
import com.handpay.view.ActionBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by haohz on 2018/3/14.
 */

public class DeviceInfoActivity extends BaseActivity {
    private ScrollView mScrollView;
    private static final int timeInterval = 100;
    private TextView mCpu, mCpuPercent, mVersion, mResolution, mBrightness, mRamTotal, mUsedRam, mAvailableRam, mAvailableRamPercent, mSd, mAvailableSd, mManufacturer;
    private Task cpuTask;
    private boolean running = true;
    private String cpuPercent;
    private String cpuBoard;
    private TextView mModel;
    private int brightness;
    private String line, free, cached, memTotal, ramSize, ramFreeSize, ramUsedSize;
    private double total;
    private double memFree;
    private double memUsed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_device_info);
        super.onCreate(savedInstanceState);
        mScrollView = findViewById(R.id.scroll_view);

        LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.view_device_info, null);
        mScrollView.addView(ll);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(cpuRunnable, timeInterval);
    }

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler();
    private Runnable cpuRunnable = new Runnable() {
        @Override
        public void run() {
            getDeviceInfo();
            getRAMInfo();
            setView();
            mHandler.postDelayed(cpuRunnable, timeInterval);

        }
    };

    private void removeHandler() {
        LogT.w("移除handler");
        if (mHandler != null) {
            mHandler.removeCallbacks(cpuRunnable);//移除回调
            cpuRunnable = null;
            mHandler = null;
        }

    }

    private void getDeviceInfo() {
        brightness = CommonUtils.getScreenBrightness(DeviceInfoActivity.this);
        cpuPercent = CommonUtils.getCpuUsagePer("cpu");

    }

    @SuppressLint("SetTextI18n")
    private void setView() {
        mCpuPercent.setText(Html.fromHtml("CPU使用率:<font color='#7F7F7F'><middle>" + cpuPercent + "</middle></font>"));
        mBrightness.setText(Html.fromHtml("屏幕亮度:<font color='#7F7F7F'><middle>" + brightness + "</middle></font>"));
        if (total > 1024) {
            mRamTotal.setText(Html.fromHtml("RAM总容量:<font color='#7F7F7F'><middle>" + CommonUtils.parseDouble(total / 1024.0) + "GB" + "</middle></font>"));
        } else
            mRamTotal.setText(Html.fromHtml("RAM总容量:<font color='#7F7F7F'><middle>" + CommonUtils.parseDouble(total) + "MB" + "</middle></font>"));
        if (memFree > 1024)
            mAvailableRam.setText(Html.fromHtml("可用的RAM容量:<font color='#7F7F7F'><middle>" + CommonUtils.parseDouble(memFree / 1024.0) + "GB" + "</middle></font>"));
        else
            mAvailableRam.setText(Html.fromHtml("可用的RAM容量:<font color='#7F7F7F'><middle>" + CommonUtils.parseDouble(memFree) + "MB" + "</middle></font>"));
        if (memUsed > 1024)
            mUsedRam.setText(Html.fromHtml("已使用的RAM:<font color='#7F7F7F'><middle>" + CommonUtils.parseDouble(memUsed / 1024.0) + "GB" + "</middle></font>"));
        else
            mUsedRam.setText(Html.fromHtml("已使用的RAM:<font color='#7F7F7F'><middle>" + CommonUtils.parseDouble(memUsed) + "MB" + "</middle></font>"));
        mAvailableRamPercent.setText(Html.fromHtml("可用的RAM比例:<font color='#7F7F7F'><middle>" + CommonUtils.parseDoubletoPer(memFree / total) + "</middle></font>"));

    }

    private void initView() {
        mCpu = findViewById(R.id.tv_cpu);
        mCpuPercent = findViewById(R.id.tv_cpu_percent);
//        mManufacturer = findViewById(R.id.tv_manufacturer);
        mModel = findViewById(R.id.tv_model);
        mVersion = findViewById(R.id.tv_version);
        mResolution = findViewById(R.id.tv_resolution);
        mBrightness = findViewById(R.id.tv_brightness);
        mRamTotal = findViewById(R.id.tv_ram_total);
        mUsedRam = findViewById(R.id.tv_used_ram);
        mAvailableRam = findViewById(R.id.tv_available_ram);
        mAvailableRamPercent = findViewById(R.id.tv_available_ram_percent);
        mSd = findViewById(R.id.tv_sd);
        mAvailableSd = findViewById(R.id.tv_available_sd);

        mCpu.setText(Html.fromHtml("CPU:<font color='#7F7F7F'><middle>" + CommonUtils.getBoard() + "</middle></font>"));
        mModel.setText(Html.fromHtml("设备型号:<font color='#7F7F7F'><middle>" + android.os.Build.MODEL + "</middle></font>"));
        mVersion.setText(Html.fromHtml("Android 系统版本:<font color='#7F7F7F'><middle>" + CommonUtils.getVersionRelease() + "</middle></font>"));
        mResolution.setText(Html.fromHtml("分辨率:<font color='#7F7F7F'><middle>" + CommonUtils.getResolution(DeviceInfoActivity.this) + "</middle></font>"));
        getSdcardSize();
    }

    private void getRAMInfo() {
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/meminfo");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine()) != null) {
                if (line.trim().length() < 1) {
                    continue;
                } else if (line.contains("MemFree")) {
                    free = line.split(":")[1].split("kB")[0].trim();
                } else if (line.contains("Cached")) {
                    cached = line.split(":")[1].split("kB")[0].trim();
                    break;
                } else if (line.contains("MemTotal")) {
                    memTotal = line.split(":")[1].split("kB")[0].trim();
                }
            }
            memFree = Double.parseDouble(free) + Double.parseDouble(cached);
            memFree = memFree / 1024;
            total = Double.parseDouble(memTotal) / 1024;
            memUsed = total - memFree;
            ramUsedSize = CommonUtils.parseDouble(memUsed);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSdcardSize() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();
            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
            long nTotalBlocks = statfs.getBlockCount();
            long nBlocSize = statfs.getBlockSize();
            long nAvailaBlock = statfs.getAvailableBlocks();
            long nFreeBlock = statfs.getFreeBlocks();
            long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
            long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
            if (nSDFreeSize > 1024)
                mAvailableSd.setText(Html.fromHtml("SD卡剩余容量:<font color='#7F7F7F'><middle>" +
                        nSDFreeSize / 1024 + "." + nSDFreeSize % 1024 / 10 + "GB" + "</middle></font>"));
            else
                mAvailableSd.setText(Html.fromHtml("SD卡剩余容量:<font color='#7F7F7F'><middle>" +
                        nSDFreeSize + "MB" + "</middle></font>"));

            if (nSDTotalSize > 1024)
                mSd.setText(Html.fromHtml("SD卡总容量:<font color='#7F7F7F'><middle>" + nSDTotalSize / 1024 + "." + nSDTotalSize % 1024 / 10 + "GB" + "</middle></font>"));
            else
                mSd.setText(Html.fromHtml("SD卡总容量:<font color='#7F7F7F'><middle>" + nSDTotalSize + "MB" + "</middle></font>"));

        } else {
            mSd.setVisibility(View.GONE);
            mAvailableSd.setVisibility(View.GONE);
            mSd.setText(Html.fromHtml("SD卡总容量:<font color='#7F7F7F'><middle>" + "SD卡未加载" + "</middle></font>"));
            mAvailableSd.setText(Html.fromHtml("SD卡剩余容量:<font color='#7F7F7F'><middle>" + "SD卡未加载" + "</middle></font>"));
        }
    }

    public ActionBar getActivityActionBar() {
        return new ActionBar("设备信息", true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHandler();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        removeHandler();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        removeHandler();
        super.onStop();
    }
}