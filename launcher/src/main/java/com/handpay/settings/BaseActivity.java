package com.handpay.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

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
}
