package com.handpay.config;

/**
 * Created by jmshuai on 2014/4/30.
 */
public class CustomerExceptionHandler implements Thread.UncaughtExceptionHandler {
    //Thread.UncaughtExceptionHandler mDefault;
    public CustomerExceptionHandler() {
        //mDefault = Thread.getDefaultUncaughtExceptionHandler();
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
    	// 系统的崩溃日志，还是打印出来吧！
//        HPLog.e("CustomerExceptionHandler", thread.getName(), ex);
//        //上传程序异常日志
//        MobclickAgent.reportError(ZZTApplication.getApp(), ex);
//        ZZTApplication.getApp().exitApp();
        System.exit(0);
        //mDefault.uncaughtException(thread, ex);
    }
}
