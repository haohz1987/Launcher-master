package com.handpay.config;

import com.handpay.safe.SecureManager;

public class LauncherConfig {

    public static String APPSERVER;             //  服务地址
    public static String CHANNEL;               //  渠道
    public static String HPVIRSION;             //  协议版本号
    public static boolean isUseHTTPS = false;   //  是否使用双向认证
    public static final String URL_PATH = "hpayMicroView/";

    // for test
    public static boolean isTesting = false;
    public static final boolean isTestLocation = false;//是否允许输入经纬度。
    public static final boolean isTestSelectLocation = false;
    public static String testerCsn = "";
    public static boolean SELECT_DEVICE_TYPE = true;
    public static TestEnv ENV;
    public static String SECRETKEY = null;


    public static class TestEnv {

        public String SERVER;               // 服务端地址
        public int PORT;                    // 服务端口号
        public String HPV;                  // 服务版本号
        public String CSN;                  // 使用的csn
        public boolean CANSET;              // 是否允许自己设置配置
        public boolean SWIPERCSN;           // 是否使用刷卡器csn
        public String CHAN;                 // 渠道号
        public boolean BALANCE_ENQUIRE_UP;  // 标志余额查询，使用银联与否
        public boolean PRINTLOG = false;    // 是否打印log
        public boolean REPLACEDOMAIN = false;  // 是否需要替换地址
        public static String UPDATE_CHANNEL;// 版本升级渠道
        // 是否激活Activity,在调用其他功能时，不使用APP的功能
        public static boolean isActive = false;
        public native static String[] stringsFromJNI();
        public static boolean isHttpsDouble = true;
        static {
            System.loadLibrary("hp_native");
            String[] strings = stringsFromJNI();
            APPSERVER = strings[0]; // 生产环境网址
            CHANNEL = strings[1]; // 渠道号
            HPVIRSION = strings[2]; // 协议版本号
            SecureManager.sMod = strings[3];
            SecureManager.sPubExp = strings[4];

            // 测试环境-172
            ENV = new TestEnv("10.148.181.132", 8080, HPVIRSION, CHANNEL, "", true, true);

//            //生产-双向认证
//            ENV = new TestEnv("https://mpay.handpay.cn/hpaySft", 8080, HPVIRSION, CHANNEL, "", false, true);
//            isHttpsDouble=true;
//
//            //生产-单向认证
//            ENV = new TestEnv("https://safepay.handpay.cn/hpaySft", 8080, HPVIRSION, CHANNEL, "", false, true);
//            isHttpsDouble=false;

            // 1表示需要选择
            SELECT_DEVICE_TYPE = "1".equals(strings[16]);
            UPDATE_CHANNEL = strings[17];
            testerCsn = ENV.CSN;
        }

        TestEnv(String ser, int port, String hpversion, String channel, String csn, boolean canSet, boolean useSwiperCsn) {
            SERVER = ser;
            PORT = port;
            HPV = hpversion;
            CHAN = channel;
            CSN = csn;
            CANSET = canSet;
            SWIPERCSN = useSwiperCsn;
            BALANCE_ENQUIRE_UP = false;
            // 非生产地址，打印log，并设置银联测试地址；生产地址，不打印log，设置银联生产地址
            if (!APPSERVER.contains(ser)) {
                PRINTLOG = true;
                isUseHTTPS = false;
                isTesting = true;
            } else {
                PRINTLOG = true;
                isUseHTTPS = true;
                isTesting = false;
            }
        }
    }

}
