
#include <string.h>
#include <jni.h>
#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "NativeEngine", __VA_ARGS__)

jobjectArray
Java_com_handpay_config_NativeEngine_loadResources(JNIEnv *env, jobject thiz) {
    jstring str;
    jsize len = 9;
    jobjectArray args = 0;
    char *sa[] = {"https://x.y.z/hp",           //  0生产环境
                  "通用渠道",                      //  1渠道
                  "3.5",                        //  2协议版本号
                  "DES加解密_publicKey",     //  3DES加解密_publicKey
                  "DES加解密_publicExp",     //  4DES加解密_publicExp
                  "x.y.z",                      //  5域名（同生产环境）
                  "HPLAUNCHER",                 //  6当前应用自定义保存文件的根文件夹
                  "1",                          //  7是否需要选择设备
                  "升级渠道",                      //  8升级渠道
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
