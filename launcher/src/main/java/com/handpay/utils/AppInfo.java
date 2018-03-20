package com.handpay.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {
	public String pkgName;//包名
	public String appName;//应用名
	public Drawable appIcon;//应用图标
	public Intent appIntent;//应用启动Intent
	public String switchInfo;//描述
	public int flag = 1;//0代表老应用 1代表新应用 ，默认1
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	//追加一个版本号，
	public String versionCode;//这个一般我们不会改变
	public String versionName;//1.0.0 我们一般会更新这个参数来更新本地版本
	
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public Intent getAppIntent() {
		return appIntent;
	}
	public void setAppIntent(Intent appIntent) {
		this.appIntent = appIntent;
	}
	public String getSwitchInfo() {
		return switchInfo;
	}
	public void setSwitchInfo(String switchInfo) {
		this.switchInfo = switchInfo;
	}
	
	
	
}
