package com.handpay.settings;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handpay.launch.hp.R;
import com.handpay.launch.util.LogT;
import com.handpay.view.ActionBar;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class LoadProgressActivity extends BaseActivity {

    private TextView tv_type, tv_now_speed, tv_ave_speed;
    private Button btn;
    private ImageView needle;
    private Info info;
    private byte[] imageBytes;
    private boolean flag;
    private int last_degree = 0, cur_degree;

    double TIME_SPAN = 2000d;
    private long rxtxTotal = 0;
    private long mobileRecvSum = 0;
    private long mobileSendSum = 0;
    private long wlanRecvSum = 0;
    private long wlanSendSum = 0;
    private long exitTime = 0;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                tv_now_speed.setText(msg.arg1 + "KB/S");
                tv_ave_speed.setText(msg.arg2 + "KB/S");
                startAnimation(msg.arg1);
            }
            if (msg.what == 0x100) {
                tv_now_speed.setText("0KB/S");
                startAnimation(0);
                btn.setText("重新测试");
                btn.setEnabled(true);
                flag = false;
            }
        }
    };
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_net_speed);
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        info = new Info();
        tv_type = (TextView) findViewById(R.id.connection_type);
        tv_now_speed = (TextView) findViewById(R.id.now_speed);
        tv_ave_speed = (TextView) findViewById(R.id.ave_speed);
        needle = (ImageView) findViewById(R.id.needle);
        btn = (Button) findViewById(R.id.start_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View arg0) {
                flag = true;
                tv_type.setText(connectivityManager.getActiveNetworkInfo().getTypeName());
                btn.setText("测试中");
                btn.setEnabled(false);
                info.hadfinishByte = 0;
                info.speed = 0;
                info.totalByte = 1024 * 10;
                new DownloadThread().start();
                new GetInfoThread().start();
            }
        });
    }
    public ActionBar getActivityActionBar() {
        return new ActionBar("实时网速", true);
    }

    class DownloadThread extends Thread {

        @Override
        public void run() {
            String url_string = "http://down.handmart.cn/smartpos/N900/SmartPosN900.apk ";
            long start_time, cur_time;
            URL url;
            URLConnection connection;
            InputStream iStream;

            try {
                url = new URL(url_string);
                connection = url.openConnection();
                info.totalByte = connection.getContentLength();
                LogT.w("info.totalByte=" + info.totalByte);

                iStream = connection.getInputStream();
                start_time = System.currentTimeMillis();
                while (iStream.read() != -1 && flag) {

                    info.hadfinishByte++;
                    cur_time = System.currentTimeMillis();
                    if (cur_time - start_time == 0) {
                        info.speed = 1000;
                    } else {
                        info.speed = info.hadfinishByte / (cur_time - start_time) * 1000;
                    }
                }
                iStream.close();
            } catch (Exception e) {
                LogT.w("DownloadThread_e=" + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    class GetInfoThread extends Thread {

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            // TODO Auto-generated method stub
            double sum, counter;
            int cur_speed, ave_speed;

            long tempSum = TrafficStats.getTotalRxBytes()
                    + TrafficStats.getTotalTxBytes();
            long rxtxLast = tempSum - rxtxTotal;
            double totalSpeed = rxtxLast * 1000 / TIME_SPAN;
            rxtxTotal = tempSum;
            long tempMobileRx = TrafficStats.getMobileRxBytes();//流量下载
            long tempMobileTx = TrafficStats.getMobileTxBytes();//流量上传
            long tempWlanRx = TrafficStats.getTotalRxBytes() - tempMobileRx;
            long tempWlanTx = TrafficStats.getTotalTxBytes() - tempMobileTx;
            long mobileLastRecv = tempMobileRx - mobileRecvSum;
            long mobileLastSend = tempMobileTx - mobileSendSum;
            long wlanLastRecv = tempWlanRx - wlanRecvSum;
            long wlanLastSend = tempWlanTx - wlanSendSum;
            double mobileRecvSpeed = mobileLastRecv * 1000 / TIME_SPAN;
            double mobileSendSpeed = mobileLastSend * 1000 / TIME_SPAN;
            double wlanRecvSpeed = wlanLastRecv * 1000 / TIME_SPAN;
            double wlanSendSpeed = wlanLastSend * 1000 / TIME_SPAN;
            mobileRecvSum = tempMobileRx;
            mobileSendSum = tempMobileTx;
            wlanRecvSum = tempWlanRx;
            wlanSendSum = tempWlanTx;
            if (mobileRecvSpeed >= 0d && connectivityManager.getActiveNetworkInfo().getTypeName().contains("MOBILE")) {
                info.speed = mobileRecvSpeed/1024d;
            }
            if (wlanRecvSpeed >= 0d  && connectivityManager.getActiveNetworkInfo().getTypeName().contains("WIFI")) {
                info.speed = wlanRecvSpeed/1024d;
            }
            try {
                sum = 0;
                counter = 0;
                while (flag && (((int) counter) < 25) && (info.hadfinishByte < info.totalByte)) {
                    Thread.sleep(250);
                    sum += info.speed;
                    counter++;
                    cur_speed = (int) info.speed;
                    ave_speed = (int) (sum / counter);
                    LogT.w("cur_speed:" + info.speed / 1024 + "KB/S ave_speed:" + ave_speed / 1024);
                    Message msg = new Message();
                    msg.arg1 = ((int) info.speed / 1024);
                    msg.arg2 = ((int) ave_speed / 1024);
                    msg.what = 0x123;
                    handler.sendMessage(msg);
                }
                if (flag && (((int) counter) == 25) || (info.hadfinishByte < info.totalByte)) {
                    handler.sendEmptyMessage(0x100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private String showSpeed(double speed) {
        String speedString;
        if (speed >= 1048576d) {
            speedString = showFloatFormat.format(speed / 1048576d) + "MB/s";
        } else {
            speedString = showFloatFormat.format(speed / 1024d) + "KB/s";
        }
        return speedString;
    }
    private DecimalFormat showFloatFormat = new DecimalFormat("0.00");

    @Override
    public void onBackPressed() {
        flag = false;
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        flag = true;
        super.onResume();
    }

    private void startAnimation(int cur_speed) {
        cur_degree = getDegree(cur_speed);
        RotateAnimation rotateAnimation = new RotateAnimation(last_degree, cur_degree, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(1000);
        last_degree = cur_degree;
        needle.startAnimation(rotateAnimation);
    }

    private int getDegree(double cur_speed) {
        int ret = 0;
        if (cur_speed >= 0 && cur_speed <= 512) {
            ret = (int) (15.0 * cur_speed / 128.0);
        } else if (cur_speed >= 512 && cur_speed <= 1024) {
            ret = (int) (60 + 15.0 * cur_speed / 256.0);
        } else if (cur_speed >= 1024 && cur_speed <= 10 * 1024) {
            ret = (int) (90 + 15.0 * cur_speed / 1024.0);
        } else {
            ret = 180;
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        flag = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    class Info {
        double speed;
        int hadfinishByte;
        int totalByte;
    }
}
