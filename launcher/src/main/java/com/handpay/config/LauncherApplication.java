package com.handpay.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.handpay.launch.Launcher;
import com.handpay.launch.LauncherAppState;
import com.handpay.launch.util.LogT;

import java.util.Stack;

/**
 * 在launcher.java中定义了单例类LauncherAppState，调用
 * LauncherAppState.setApplicationContext(getApplicationContext());
 * LauncherAppState app = LauncherAppState.getInstance();
 * 由于launcher3z只有一个activity，LauncherApplication不需要调用
 */
public class LauncherApplication extends Application {
    private static LauncherApplication lcApp;
    private static Stack<Activity> activityStack;
    public static Context mContext;
    private SharedPreferences mSharedPrefs;

    public static LauncherApplication getInstance() {
        if (lcApp == null) {
            lcApp = new LauncherApplication();
        }
        return lcApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lcApp = this;
        mContext = getApplicationContext();
        mSharedPrefs = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),Context.MODE_PRIVATE);
        if (LauncherConfig.isUseHTTPS) {
            String x = LauncherConfig.ENV.TEV;
//            ClientEngine.getInstance().init(this, SmartPosConfig.ENV.HPV, "https://" + SmartPosConfig.ENV.SERVER.trim() + "/hpayMicroView/", SmartPosConfig.ENV.CHAN);
//        } else {
//            ClientEngine.getInstance().init(this, SmartPosConfig.ENV.HPV, "http://" + SmartPosConfig.ENV.SERVER.trim() + ":" + SmartPosConfig.ENV.PORT + "/hpayMicroView/", SmartPosConfig.ENV.CHAN);
        }
        LogT.init(true, Log.VERBOSE);//不输出到文件
//        Thread.setDefaultUncaughtExceptionHandler(new CustomerExceptionHandler());
        LauncherAppState.setApplicationContext(this);
        LauncherAppState.getInstance();

    }

    /* 使用Application如果保存了一些不该保存的对象很容易导致内存泄漏。
    如果在Application的oncreate中执行比较 耗时的操作，将直接影响的程序的启动时间。
    一些清理工作不能依靠onTerminate完成，因为android会尽量让你的程序一直运行，
    所以很有可能 onTerminate不会被调用。
    当终止应用程序对象时调用，不保证一定被调用，当程序是被内核终止以便为其他应用程序释放
    资源，那么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程 */
    @Override
    public void onTerminate() {
        super.onTerminate();
        LauncherAppState.getInstance().onTerminate();
        AppExit();
    }

    /* 当后台程序已经终止资源还匮乏时会调用这个方法。好的应用程序一般会在这个方法
    里面释放一些不必要的资源来应付当后台程序已经终止，前台应用程序内存还不够时的情况。 */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack.size() > 0 && null != activityStack.lastElement()) {
            return activityStack.lastElement();
        } else {
            return null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 清除非当前activity
     */
    public void AppExit() {
        mSharedPrefs.edit().putBoolean(Launcher.SET_ENV_DISMISSED, false).apply();
        try {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        } catch (Exception e) {
            LogT.w("软件没有正常退出" + e.getMessage());
        }
        LogT.w("当前进程=" + android.os.Process.myPid() + ",当前包名=" + getPackageName());
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            am.killBackgroundProcesses("com.handpay.launch.WallpaperPickerActivity");
            am.killBackgroundProcesses("com.handpay.launch.WallpaperCropActivity");
//            am.killBackgroundProcesses("com.handpay.settings.SettingsActivity");
            am.killBackgroundProcesses(getPackageName());
        }
        /* 这两句不加也可，有些手机会黑屏 */
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
