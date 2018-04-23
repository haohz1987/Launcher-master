package com.handpay.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

import com.handpay.config.LauncherApplication;
import com.handpay.launch.hp.R;
import com.handpay.utils.Heart;
import com.handpay.view.AbTitleBar;
import com.handpay.view.ActionBar;

/**
 * Created by haohz on 2018/3/14.
 */

public class BaseActivity extends Activity {
    private AbTitleBar myActionBar;

    protected boolean isShownTopBar() {
        return true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        LauncherApplication.getInstance().addActivity(BaseActivity.this);
        Heart.initialize(this);
        myActionBar = (AbTitleBar) this.findViewById(R.id.top_actionbar);
        if (isShownTopBar()) {
            if (myActionBar != null) {
                myActionBar.ininTitleBar(this);
                myActionBar.refreshActionBar(getActivityActionBar());
                initActionBarAndTabHeight();
                if (!isVisiableTopBar()) {
                    myActionBar.setVisibility(View.GONE);
                }
            } else {
                throw new RuntimeException("please use top_bar.xml");
            }
        }
    }
    // 设置标题 按钮 事件
    public ActionBar getActivityActionBar() {
        return null;
    }
    /**
     * @return
     */
    protected boolean isVisiableTopBar() {
        return true;
    }

    /**
     * 初始化ActionBar 和 Tab 的高度
     */
    public void initActionBarAndTabHeight() {
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
            android.view.ViewGroup.LayoutParams params = myActionBar.getLayoutParams();
            params.height = actionBarHeight;
            myActionBar.setLayoutParams(params);
        }
    }
    public void startActivityPending(Context cxt,Class clazz){
        startActivity(new Intent(cxt,clazz));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
    }
    public void startActivityPending(Context cxt,String action){
        cxt.startActivity(new Intent(action));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);// 从左向右滑入的效果
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LauncherApplication.getInstance().finishActivity(BaseActivity.this);
    }
}
