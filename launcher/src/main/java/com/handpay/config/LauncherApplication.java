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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

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
        initOkgo();
    }

    private void initOkgo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");
        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        //HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(1)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers)                      //全局公共头
                .addCommonParams(params);                       //全局公共参数
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
    /**
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 重要的事情说三遍，以下代码不要直接使用
     */
    private class SafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                for (X509Certificate certificate : chain) {
                    certificate.checkValidity(); //检查证书是否过期，签名是否通过等
                }
            } catch (Exception e) {
                throw new CertificateException(e);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 重要的事情说三遍，以下代码不要直接使用
     */
    private class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //验证主机名是否匹配
            //return hostname.equals("server.jeasonlzy.com");
            return true;
        }
    }
}
