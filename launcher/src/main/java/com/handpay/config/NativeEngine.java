package com.handpay.config;

import com.handpay.launch.Launcher;
import com.handpay.launch.util.LogT;

@SuppressWarnings("JniMissingFunction")
public class NativeEngine {
    public static final String TAG = "NativeEngine";

    static {
        System.loadLibrary("launcher_native");
    }

    public native static String[] loadResources();

    public static NativeObject initNativeLib() {
        String[] tempResources = loadResources();
        NativeObject nativeObject = new NativeObject();
        if (tempResources != null) {
            nativeObject.setAppServer(tempResources[0]);
            nativeObject.setChannel(tempResources[1]);
            nativeObject.setVersion(tempResources[2]);
            nativeObject.setModulus(tempResources[3]);
            nativeObject.setExponent(tempResources[4]);
            nativeObject.setDomain(tempResources[5]);
            nativeObject.setRootList(tempResources[6]);
            nativeObject.setSelectDevice(tempResources[7]);
            nativeObject.setClientChannel(tempResources[8]);
        }
        if (Launcher.DEBUG) LogT.w("获取的jni参数：" + nativeObject.toString());
        return nativeObject;
    }

    static class NativeObject {
        private String AppServer;//生产环境地址
        private String Channel;//渠道号
        private String Version;//协议版本号
        private String Modulus;//rsa公钥系数(模数/key)
        private String Exponent;//rsa公钥指数
        private String Domain;//服务器域名
        private String RootList;//保存文件的根目录
        private String SelectDevice;//是否需要选择设备
        private String ClientChannel;//客户端升级渠道号（预留）

        public String getAppServer() {
            return AppServer;
        }

        public void setAppServer(String appServer) {
            AppServer = appServer;
        }

        public String getChannel() {
            return Channel;
        }

        public void setChannel(String channel) {
            Channel = channel;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String version) {
            Version = version;
        }

        public String getModulus() {
            return Modulus;
        }

        public void setModulus(String modulus) {
            Modulus = modulus;
        }

        public String getExponent() {
            return Exponent;
        }

        public void setExponent(String exponent) {
            Exponent = exponent;
        }

        public String getDomain() {
            return Domain;
        }

        public void setDomain(String domain) {
            Domain = domain;
        }

        public String getRootList() {
            return RootList;
        }

        public void setRootList(String rootList) {
            RootList = rootList;
        }

        public String getSelectDevice() {
            return SelectDevice;
        }

        public void setSelectDevice(String selectDevice) {
            SelectDevice = selectDevice;
        }

        public String getClientChannel() {
            return ClientChannel;
        }

        public void setClientChannel(String clientChannel) {
            ClientChannel = clientChannel;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"Version\":\"")
                    .append(Version).append('\"');
            sb.append(",\"version\":\"")
                    .append(getVersion()).append('\"');
            sb.append(",\"SelectDevice\":\"")
                    .append(SelectDevice).append('\"');
            sb.append(",\"selectDevice\":\"")
                    .append(getSelectDevice()).append('\"');
            sb.append(",\"RootList\":\"")
                    .append(RootList).append('\"');
            sb.append(",\"rootList\":\"")
                    .append(getRootList()).append('\"');
            sb.append(",\"Modulus\":\"")
                    .append(Modulus).append('\"');
            sb.append(",\"modulus\":\"")
                    .append(getModulus()).append('\"');
            sb.append(",\"Exponent\":\"")
                    .append(Exponent).append('\"');
            sb.append(",\"exponent\":\"")
                    .append(getExponent()).append('\"');
            sb.append(",\"Domain\":\"")
                    .append(Domain).append('\"');
            sb.append(",\"domain\":\"")
                    .append(getDomain()).append('\"');
            sb.append(",\"ClientChannel\":\"")
                    .append(ClientChannel).append('\"');
            sb.append(",\"clientChannel\":\"")
                    .append(getClientChannel()).append('\"');
            sb.append(",\"Channel\":\"")
                    .append(Channel).append('\"');
            sb.append(",\"channel\":\"")
                    .append(getChannel()).append('\"');
            sb.append(",\"AppServer\":\"")
                    .append(AppServer).append('\"');
            sb.append(",\"appServer\":\"")
                    .append(getAppServer()).append('\"');
            sb.append('}');
            return sb.toString();
        }
    }
}
