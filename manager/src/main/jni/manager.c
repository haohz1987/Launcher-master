#include <string.h>
#include <jni.h>
//#include <android/log.h>
//#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "NativeEngine", __VA_ARGS__)

jobjectArray
Java_com_handpay_config_NativeEngine_loadResources(JNIEnv *env, jobject thiz) {
    jstring str;
    jsize len = 9;
    jobjectArray args = 0;
    char *sa[] = {"https://safesft.handpay.cn/hpayMicroView",           //  0生产环境
                  "HPZZT",                      //  1渠道
                  "3.5",                        //  2协议版本号
                  "publicKey",     //  3DES加解密_publicKey
                  "publicExp",     //  4DES加解密_publicExp
                  "safesft.handpay.cn",                      //  5域名（同生产环境）
                  "HP_LAUNCHER",                 //  6当前应用自定义保存文件的根文件夹
                  "0",                          //  7是否需要选择设备
                  "(预留)升级渠道",                      //  8升级渠道
    };

    len = sizeof(sa) / sizeof(sa[0]);
    int i = 0;
    args = (*env)->NewObjectArray(env, len, (*env)->FindClass(env, "java/lang/String"), 0);
    for (i = 0; i < len; i++) {
        str = (*env)->NewStringUTF(env, sa[i]);
        (*env)->SetObjectArrayElement(env, args, i, str);
    }
    return args;
}

//测试
//public static String APPSERVER = "http://10.148.181.177:8080/hpayMicroView";//177测试环境
// 生产 hpayMicroView 双向
//	public static String APPSERVER = "https://mpay.handpay.cn/hpaySft";//生产环境 单向认证	 打开IsHttpsOne																																																											// 生产
//	public static String APPSERVER = "https://safepay.handpay.cn/hpayMicroView";//生产环境 双向认证 打开IsHttpsDouble，A系统,需要改成版本号2.0.1
//    public static String APPSERVER = "https://safesft.handpay.cn/hpayMicroView";//生产环境 双向认证 打开IsHttpsDouble，C系统，需改成safesft，版本号2.0.0