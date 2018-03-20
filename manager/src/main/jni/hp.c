
#include <string.h>
#include <jni.h>
#include <android/log.h>
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "shuai", __VA_ARGS__)
jobjectArray
Java_com_handpay_config_LauncherConfig_stringsFromJNI(JNIEnv* env,jobject thiz )
{
	  jstring str;
	  jsize len = 21;
	  jobjectArray args = 0;
	  char* sa[] = {  "https://safepay.handpay.cn/hpaySft",     //生产环境
					  "HPZZT",                                  //渠道
					  "3.5",                                    // 协议版本号
					  // DES加解密公钥_publicKey
					  "DES加解密公钥_publicKey",
					  "DES加解密公钥_publicExp",                                  // DES加解密公钥_publicExp
					  "HPLAUNCHER",                             //当前应用自定义保存文件的根文件夹
					  "1",                                      // 是否需要选择设备
                      "HPZZT",                                  // 升级渠道
					  };

	  int i=0;
	  args = (*env)->NewObjectArray(env,len,(*env)->FindClass(env,"java/lang/String"),0);
	  for( i=0; i < len; i++ )
	  {
	  str = (*env)->NewStringUTF(env,sa[i] );
	  (*env)->SetObjectArrayElement(env,args, i, str);
	  }
	  return args;
}
    //测试
//    public static String APPSERVER = "http://10.148.181.132:8080/hpaySft";//132测试环境
    //测试
//    public static String APPSERVER = "http://10.148.181.172:8080/hpaySft";//172测试环境
    // 生产
//	public static String APPSERVER	= "https://mpay.handpay.cn/hpaySft";//生产环境 单向认证	 打开IsHttpsOne																																																											// 生产
//	public static String APPSERVER	= "https://safepay.handpay.cn/hpaySft";//生产环境 双向认证 打开IsHttpsDouble