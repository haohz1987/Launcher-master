package com.handpay.config;

import com.handpay.safe.SecureManager;

public class LauncherConfig {

    public static String SERVER; // 服务端生产地址
    public static String CHANNEL;// 渠道
    public static String VERSION;// 渠道
    public static String ROOTLIST;//文件根目录
    public static String CLIENT_CHANNEL;//升级渠道
    public static String SELECET_DEVICE;//是否需要选择设备，智能pos需要后台配置SN码
    public static boolean isUseHTTPS = true;   //  是否使用双向认证
    public static final String SERVER_PATH = "/hp/";//次级地址
    // 是否激活Activity,在调用其他功能时，不使用APP的功能
    public static boolean isActive = false;

    public static TestEnv ENV;
    /* 测试 */
    public static String TestCsn = "";

    static {
        NativeEngine.NativeObject nativeObject = NativeEngine.initNativeLib();
        SERVER = nativeObject.getAppServer();
        CHANNEL = nativeObject.getChannel();
        VERSION = nativeObject.getVersion();
        SecureManager.sMod = nativeObject.getModulus();
        SecureManager.sPubExp = nativeObject.getExponent();
        ROOTLIST = nativeObject.getRootList();
        CLIENT_CHANNEL = nativeObject.getClientChannel();
        SELECET_DEVICE = nativeObject.getSelectDevice();


        //生产环境
        ENV = new TestEnv(nativeObject.getDomain(), 80, VERSION, CHANNEL, "", false, true);
        // 测试环境-177
        ENV = new TestEnv("10.148.181.177", 8080, VERSION, CHANNEL, "", true, true);

        TestCsn = ENV.TECSN;

    }

    public static class TestEnv {
        public String TESERVER; // 服务端地址
        public int PORT;                    // 服务端口号
        public String TEV;                  // 服务版本号
        public String TECSN;                  // 使用的csn
        public boolean CANSET;              // 是否允许自己设置配置
        public boolean SWIPERCSN;           // 是否使用刷卡器csn
        public String TECHANNEL;                 // 渠道号
        public boolean BALANCE_ENQUIRE_UP;  // 标志余额查询，使用银联与否
        public boolean PRINTLOG = false;    // 是否打印log
        public boolean REPLACEDOMAIN = false;  // 是否需要替换地址

        TestEnv(String ser, int port, String version, String channel, String csn, boolean canSet, boolean useSwiperCsn) {
            TESERVER = ser;
            PORT = port;
            TEV = version;
            TECHANNEL = channel;
            TECSN = csn;
            CANSET = canSet;
            SWIPERCSN = useSwiperCsn;
            BALANCE_ENQUIRE_UP = false;
            // 非生产地址，打印log，并设置银联测试地址；生产地址，不打印log，设置银联生产地址
            if (!SERVER.contains(ser)) {
                PRINTLOG = true;
                isUseHTTPS = false;
            } else {
                PRINTLOG = false;
                isUseHTTPS = true;
            }
        }
    }
}
