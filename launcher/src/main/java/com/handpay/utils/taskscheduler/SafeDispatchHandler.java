package com.handpay.utils.taskscheduler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.handpay.launch.hp.BuildConfig;
import com.handpay.launch.util.LogT;


/**
 *
 * @author SilenceDut
 * @date 17/04/18
 *
 * a safe Handler avoid crash
 */
public class SafeDispatchHandler extends Handler {

    private static final String TAG = "SafeDispatchHandler";
    public SafeDispatchHandler(Looper looper) {
        super(looper);
    }

    public SafeDispatchHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    public SafeDispatchHandler() {
        super();
    }

    public SafeDispatchHandler(Callback callback) {
        super(callback);
    }

    @Override
    public void dispatchMessage(Message msg) {
        if (BuildConfig.DEBUG) {
            super.dispatchMessage(msg);
        } else {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                LogT.d("dispatchMessage Exception " + msg + " , " + e);
            } catch (Error error) {
                LogT.d("dispatchMessage error " + msg + " , " + error);
            }
        }
    }
}
