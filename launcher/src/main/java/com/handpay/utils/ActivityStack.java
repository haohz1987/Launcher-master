package com.handpay.utils;

import android.app.Activity;

import java.util.LinkedList;


/**
 * 
 * @author lzliao
 *
 */
public class ActivityStack {

	private LinkedList<Activity> activityList = new LinkedList<Activity>();

	private ActivityStack() {
	}

	/**
	 * 单例模式中获取唯一的ExitApplication 实例
	 * 
	 * @return ExitApplication 实例
	 */
	public static ActivityStack getInstance() {
		return ActivityStackHolder.INSTANCE;
	}

	private static final class ActivityStackHolder {
		private static final ActivityStack INSTANCE = new ActivityStack();
	}

	/**
	 * 添加Activity 到容器中
	 * 
	 * @param activity
	 */
	public synchronized void addActivity(Activity activity) {
		{
			activityList.addFirst(activity);
		}
	}

	public synchronized void removeActivity(Activity activity) {
		activityList.remove(activity);
		activity.finish();
	}

	/**
	 * 遍历所有Activity 并finish
	 */
	public synchronized void finishAll() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}
	
	public synchronized LinkedList<Activity> getActivitys(){
		return activityList;
	}
}

