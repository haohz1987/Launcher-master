package com.handpay.utils;
import android.view.View;
public abstract class DebouncingOnClickListener implements View.OnClickListener {
    private static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override
        public void run() {
            enabled = true;
        }
    };

    @Override
    public final void onClick(View v) {
        if (enabled) {
            enabled = false;
            v.postDelayed(ENABLE_AGAIN,500);//延时0.5s可点击
//            v.post(ENABLE_AGAIN);
            doClick(v);
        }
    }
    public abstract void doClick(View v);
}