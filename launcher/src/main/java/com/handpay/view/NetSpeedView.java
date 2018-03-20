package com.handpay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handpay.launch.hp.R;

import java.text.DecimalFormat;

public class NetSpeedView extends FrameLayout {

    private RelativeLayout rlLayoutBig;
    private TextView tvMobileTx;
    private TextView tvMobileRx;
    private TextView tvWlanTx;
    private TextView tvWlanRx;

    private TextView MobileTx;
    private TextView MobileRx;
    private TextView WlanTx;
    private TextView WlanRx;

    double TIME_SPAN = 2000d;
    private long rxtxTotal = 0;
    private long mobileRecvSum = 0;
    private long mobileSendSum = 0;
    private long wlanRecvSum = 0;
    private long wlanSendSum = 0;
    private long exitTime = 0;

    private int mTextColor;
    private int mValueTextColor;

    private int mTextSize;

    private DecimalFormat showFloatFormat = new DecimalFormat("0.00");

    private long timeInterval = 1000;

    public NetSpeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NetSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_net_speed, this);
        rlLayoutBig = (RelativeLayout) findViewById(R.id.rlLayoutBig);
        tvMobileTx = (TextView) findViewById(R.id.tvMobileTx);
        tvMobileRx = (TextView) findViewById(R.id.tvMobileRx);
        tvWlanTx = (TextView) findViewById(R.id.tvWlanTx);
        tvWlanRx = (TextView) findViewById(R.id.tvWlanRx);

        MobileTx = (TextView) findViewById(R.id.MobileTx);
        MobileRx = (TextView) findViewById(R.id.MobileRx);
        WlanTx = (TextView) findViewById(R.id.WlanTx);
        WlanRx = (TextView) findViewById(R.id.WlanRx);

        //获得这个控件对应的属性。
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NetSpeedView);

        try {
            //获得属性值
            mTextColor = a.getColor(R.styleable.NetSpeedView_net_textcolor, getResources().getColor(R.color.black));
            mValueTextColor = a.getColor(R.styleable.NetSpeedView_netvalue_textcolor, getResources().getColor(R.color.color_download));
            mTextSize = a.getDimensionPixelSize(R.styleable.NetSpeedView_net_textsize, 12);
//            isMulti = a.getBoolean(R.styleable.NetSpeedView_isMulti, false);
        } finally {
            //回收这个对象
            a.recycle();
        }

        setTextColor(mTextColor, mValueTextColor);

        setTextSize(mTextSize);

        setMulti();

        handler.postDelayed(task, timeInterval);//延迟调用
    }

    public void setTextSize(int textSize) {
        if (textSize != 0) {
            tvMobileTx.setTextSize(textSize);
            tvMobileRx.setTextSize(textSize);
            tvWlanTx.setTextSize(textSize);
            tvWlanRx.setTextSize(textSize);

            MobileTx.setTextSize(textSize);
            MobileRx.setTextSize(textSize);
            WlanTx.setTextSize(textSize);
            WlanRx.setTextSize(textSize);

        }
    }

    public void setTextColor(int textColor, int valuetextColor) {
        if (textColor != 0 && valuetextColor != 0) {
            tvMobileTx.setTextColor(textColor);
            tvMobileRx.setTextColor(textColor);
            tvWlanTx.setTextColor(textColor);
            tvWlanRx.setTextColor(textColor);

            MobileTx.setTextColor(valuetextColor);
            MobileRx.setTextColor(valuetextColor);
            WlanTx.setTextColor(valuetextColor);
            WlanRx.setTextColor(valuetextColor);
        }
    }

    public NetSpeedView(Context context) {
        super(context);
    }

    public void updateViewData() {

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
        //==========================================================
        if (mobileRecvSpeed >= 0d) {
            tvMobileRx.setText(showSpeed(mobileRecvSpeed));
        }
        if (wlanRecvSpeed >= 0d) {
            tvWlanRx.setText(showSpeed(wlanRecvSpeed));
        }

        if (mobileSendSpeed >= 0d) {
            tvMobileTx.setText(showSpeed(mobileSendSpeed));
        }

        if (wlanSendSpeed >= 0d) {
            tvWlanTx.setText(showSpeed(wlanSendSpeed));
        }
        //==============================================================
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

    private Handler handler = new Handler();

    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, timeInterval);//设置延迟时间，此处是0.5秒
            updateViewData();
            //需要执行的代码
        }
    };

    public void setMulti() {
        rlLayoutBig.setVisibility(VISIBLE);
    }

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(task);
    }
}
