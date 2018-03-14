-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#避免内部类混淆
-keepattributes InnerClasses
#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
#使用字符串"SourceFile"来替代真正的类，避免泄漏更多的信息
#-renamesourcefileattribute SourceFile
-dontwarn android.**
-dontwarn org.luaj.**
-dontwarn javax.naming.**

-keepattributes *Annotation*
-keepattributes Signature
# 加入自定义屏蔽类 start
-keep class java.lang.annotation.** { *; }
-keep class javax.naming.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}
-dontwarn android.support.**
-dontwarn com.alibaba.fastjson.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
-keep public class android.support.v7.widget.RecyclerView
-keep public class android.support.v7.widget.RecyclerView$*{*;}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public void set*(***);
    public *** get* ();
    public *** gen*(***);
}
-keepclassmembers class * implements java.io.Serializable$*{*;}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public void set*(***);
    public *** get* ();
    public *** gen*(***);
  }
  -keepclassmembers class * implements android.os.Parcelable$*{*;}

-keep class org.xmlpull.v1.** { *; }

# umeng sdk
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.handpay.zztong.hp.R$*{
public static final int *;
}
#不混淆泛型
-keepattributes Signature

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#okgo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}

#okserver
-dontwarn com.lzy.okserver.**
-keep class com.lzy.okserver.**{*;}

## 防止百度地图
#-keep class com.baidu.** { *; }
#-keep class vi.com.gdi.bgl.** { *; }
## 防止新大陆刷卡器类混淆
#-keep class com.a.** { *; }
#-keep class com.newland.** { *; }
#-keep class com.handpay.framework.ui.** { *; }
#-keep class com.handpay.zztong.hx.ui.** { *; }
#-keep class org.apache.http.** { *; }
#-keep class com.bbpos.cswiper.** { *; }
#-keep class android_serialport_api.** { *; }
## dspread.xpos jar包
#-keep class com.dspread.xpos.** { *; }
## iBridge jar包
#-keep class com.ivt.bluetooth.ibridge.** { *; }
## emvswipeapi包
#-keep class com.bbpos.emvswiper.** { *; }
## kxml2
#-keep class com.xmlpull.v1.** { *; }
##dom4j
#-keep class org.dom4j.** { *; }
##防止酷漫刷卡器混淆
#-keep class com.mset.** { *; }
#-keep class com.mset.cardswiper.** { *; }
##防止M368防止混淆
#-keep class com.bbpos.bbdevice.**{ *; }
#-keep class com.bbpos.wisepad.**{ *; }
##防止通过地址获取网络图片混淆
#-keep class net.tsz.afinal.**{ *; }
##防止混淆获取卡号类
#-keep class com.intsig.** {*;}
##极光推送的防止混淆配置
#-dontoptimize
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#-keep  class * extends com.handpay.zztong.hp.bean.**
#-keep  class * extends com.handpay.zztong.hp.bean.**{*;}
#-keepclassmembers class * {
#    public ** onBindViewHolder(***);
#    public ** onCreateViewHolder(***);
#    public ** setOnLongClickListener(***);
#    public ** setOnClickListener(***);
#    public ** getItemViewType(***);
#    public ** onSelectItem(***);
#    }
#-keep class com.handpay.framework.NetEngineListener
#-keep class * extends com.handpay.zztong.hp.bean.ResponseBean
#-keep  class com.handpay.zztong.hp.notification.activity.NoticeActivity$**{*;}
#-keep  class com.handpay.zztong.hp.notification.activity.NoticeActivity.MyBaseAdapter$Inner{*;}
#-keep class com.handpay.zztong.hp.base.service.CoreService$*{*;}
#-keep  class com.handpay.zztong.hp.log.HPLog{*;}
##数据库业务类防止混淆
#-keep class com.handpay.zztong.hp.db.ormlite.**{*;}
## OrmLite uses reflection
#-keep class com.j256.**
#-keepclassmembers class com.j256.** { *; }
#-keep enum com.j256.**
#-keepclassmembers enum com.j256.** { *; }
#-keep interface com.j256.**
#-keepclassmembers interface com.j256.** { *; }
#-keepclassmembers class * {
#    @com.j256.ormlite.field.DatabaseField *;
#}
