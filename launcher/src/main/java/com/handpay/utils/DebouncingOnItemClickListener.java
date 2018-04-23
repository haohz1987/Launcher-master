package com.handpay.utils;

import android.view.View;
import android.widget.AdapterView;

public abstract class DebouncingOnItemClickListener implements AdapterView.OnItemClickListener {
    private static boolean enabled = true;

    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override
        public void run() {
            enabled = true;
        }
    };

    @Override
    public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (enabled) {
            enabled = false;
//            v.post(ENABLE_AGAIN);
            view.postDelayed(ENABLE_AGAIN, 500);//延时0.5s可点击
            doItemClick(parent,view,position,id);
        }
    }
    public abstract void doItemClick(AdapterView<?> parent, View view, int position, long id);
}