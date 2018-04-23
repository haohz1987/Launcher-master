package com.handpay.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.newland.telephony.TelephonyManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.handpay.settings.RespQueryAppBean;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lzliao
 */
public class CommonUtils {

    /**
     * 项目包名,先硬编码，后续修改为配置或者自动代码获取
     */
    public static final String pkgName = "com.handpay.laucher";
    /**
     * 定义一个支付应用列表，这个列表可以在后台下发控制，也可以写死在本地
     */
    public static List<String> mPayAppList = new ArrayList<String>();

    /**
     * 初始化的本机所有的app，包括系统应用，本List只能被初始化一次
     */
    public static ArrayList<AppInfo> mAppList = new ArrayList<AppInfo>();
    public static ArrayList<RespQueryAppBean> mCheckedAppList = new ArrayList<RespQueryAppBean>();
    public static ArrayList<AppInfo> mAllAppList = new ArrayList<AppInfo>();

    // 下面的这个列表可以下发下来处理
    static {
        mPayAppList.add("com.alibaba.wireless");
        mPayAppList.add("com.eg.android.AlipayGphone");
        mPayAppList.add("com.handpay.zztong.hp");
        mPayAppList.add("com.tencent.mm");
        mPayAppList.add("com.handpay.ds");
    }

    private static String TAG = "CommonUtils";
    private static FileOutputStream out = null;
    private static FileInputStream in = null;
    private static ByteArrayOutputStream bout = null;

    /*
     * 打开设置网络界面 移动网络
     */
    public static void setNetworkMethod(final Context context) {
        // 进入之前先打开网络 e
//		setMobileDataStatus(context,true);
        try {
            //在手机上测试会挂掉，添加try
            TelephonyManager teleManager = new TelephonyManager(context);
            if (!teleManager.getMobileDataEnabled()) {
                //如果当前移动数据是关闭，则 开
                teleManager.setMobileDataEnabled(true);
            }
        } catch (Exception e) {

        }
        // 跳转到移动网络设置界面,选第二个选项报错,选第一个正常,用下面的取代掉
//		context.startActivity(new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS));
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.android.phone", "com.android.phone.MobileNetworkSettings");
        intent.setComponent(component);
        context.startActivity(intent);
    }

    /**
     * 直接进入WIFI设置界面
     *
     * @param context
     */
    public static void setWifi(Context context) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_WIFI_SETTINGS));

    }

    /**
     * 直接进入蓝牙设置界面
     *
     * @param context
     */
    public static void setBlueTooth(Context context) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    /**
     * 直接进入声音设置界面
     *
     * @param context
     */
    public static void setSound(Context context) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_SOUND_SETTINGS));
    }


    /**
     * 显示当前流量使用情况,暂时无法进入
     *
     * @param context
     */
//    public static void setFlow(Context context) {
//        Intent intent = new Intent();
//        intent.setClass(context, Showmain.class);
//        context.startActivity(intent);
//    }

    /**
     * 直接进入VPN设置界面 进入之前需要设置PIN或者图案解锁，默认图案解锁 设置为字母“z” 12589几个点的顺序
     *
     * @param context
     */
    public static void setVPN(Context context) {
        Intent vpnIntent = new Intent();
        vpnIntent.setAction("android.net.vpn.SETTINGS");
        context.startActivity(vpnIntent);
    }

    /**
     * 直接进入显示设置界面，包括颜色 ，壁纸，字体，亮度，休眠，等等
     *
     * @param context
     */
    public static void setDisplay(Context context) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_DISPLAY_SETTINGS));
    }

    /**
     * 直接进入NFC设置界面
     *
     * @param context
     */
    public static void setNFC(Context context) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_NFC_SETTINGS));
    }

    /**
     * 根据name检测应用是否删除了，如果返回true说明删除了，如果false，说明没有
     *
     * @param name
     * @return
     */
    public static Boolean getAPKisUninstalled(String name) {
        // 两个固定的功能始终返回false,代表没有被删除
        if (name.equals(KEY_STRING_APPSTORE)
                || name.equals(KEY_STRING_SETTINGS_PKGNAME)) {
            return false;
        }
        Boolean flag = true;
        for (int i = 0; i < mAppList.size(); i++) {
            if (name.equals(mAppList.get(i).pkgName)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 通过反射技术处理，用于收起正在下拉的状态栏，在onWindowFocusChanged方法首行当中调用<br/>
     * 本方法可以提升到基类当中，那么所有继承基类的方法都拥有此类，并把类型修改为非static<br/>
     * 本方法还存在一个bug:当我们从预留的后门进入到系统的时候，导航条还是下拉不下来，可以添加一个全局<br/>
     * flag开关来解决，只要从后门进入，这个方法就不做任何的操作，回到咱们的应用就可以继续处理<br/>
     * 或者检测当前是否是设置界面，如果处于系统设置界面，本方法直接返回<br/>
     * 实际在有一些厂商当中诸如新大陆智能POS上本身系统深度定制的时候就屏蔽了此功能，而动联P92却没有这样做<br/>
     *
     * @param ctx
     */
    public static final void collapseStatusBar(Context ctx) {

        //在POS上，防止有的厂商会能从导航进入设置界面
        @SuppressLint("WrongConstant") Object sbservice = ctx.getSystemService("statusbar");
        try {
            Class<?> statusBarManager = Class
                    .forName("android.app.StatusBarManager");
            Method collapse;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                collapse = statusBarManager.getMethod("collapsePanels");
            } else {
                collapse = statusBarManager.getMethod("collapse");
            }
            collapse.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 这个是系统自定义的两个功能菜单
    public static final String KEY_STRING_APPSTORE = "应用中心";
    public static final String KEY_STRING_SETTINGS = "系统设置";
    public static final String KEY_STRING_SETTINGS_PKGNAME = "KEY_STRING_SETTINGS_PKGNAME";//给系统设置定义一个假的包名

    /**
     * 判断s是否为Null或者为“”或者为“ ”
     *
     * @param s
     * @return
     */
    public static Boolean isEmpty(String s) {
        if (null == s || s.isEmpty() || s.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断该包名的应用是否安装
     *
     * @param packageName
     * @return
     */
    public static boolean checkApplication(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 退出应用N900有效
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    public static void exitApp(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("com.handpay.laucher");
    }

    /**
     * 获取当前系统 厂商 型号，系统会根据这个型号来区分不同厂商，进而初始化不同厂商的设备并获得厂商实例，并且调用相应的后台数据
     */
    public static String getSysModel() {
        return android.os.Build.MODEL;
    }
    /* cpu型号 */
    public static String getBoard() {
        String value = "";
        try {
            Process p = Runtime.getRuntime().exec("getprop " + "ro.product.board");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            value = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return value.trim();
    }
    public static String getCpuUsagePer(String cpuName) {
        double per = getCpuUsagePerNum(cpuName);
        if (per == 0)
            return "offline";
        return parseDoubletoPer(per);
    }
    public static double getCpuUsagePerNum(String cpuName) {
        int[] time1 = getCpuUsage(cpuName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int[] time2 = getCpuUsage(cpuName);
        int totaltime = time2[0] - time1[0];
        int idle = time2[1] - time1[1];
        if (totaltime == 0)
            return 0;
//        LogT.w("totaltime " + totaltime + " idle " + idle);
        double per = 1 - (double) idle / (totaltime * 1.0);
        return per;
    }

    public static String getResolution(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        String resolution = height + "*" + width;
        return resolution;
    }

    public static int getScreenBrightness(Activity activity) {
        int value = 0;
        ContentResolver cr = activity.getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String getVersionRelease() {
        return getProp("ro.build.version.release");
    }

    public static String getProp(String prop) {
        String value = "";
        try {
            Process p = Runtime.getRuntime().exec("getprop " + prop);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            value = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value.trim();
    }
    public static String parseDoubletoPer(double num) {
        DecimalFormat df = new DecimalFormat("0.00%");
        String numStr = df.format(num);
        return numStr;
    }

    public static String parseDouble(double num) {
        DecimalFormat df = new DecimalFormat("#.00");
        String numStr = df.format(num);
        return numStr;
    }

    public static int[] getCpuUsage(String cpuName) {
        int total = 0, idle = 0;
        String line = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File("/proc/stat")));
            while ((line = reader.readLine()) != null) {
                String[] cpus = line.split(" ");
                if (cpus[0].trim().equals(cpuName)) {
                    for (int i = 2; i < cpus.length; i++) {
                        total += Integer.parseInt(cpus[i].trim());
                    }
                    if (cpuName.equals("cpu"))
                        idle = Integer.parseInt(cpus[5].trim());
                    else
                        idle = Integer.parseInt(cpus[4].trim());
                    break;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int times[] = {0, 0};
        times[0] = total;
        times[1] = idle;
        return times;
    }
}
