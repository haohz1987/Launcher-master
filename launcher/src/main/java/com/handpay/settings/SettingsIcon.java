package com.handpay.settings;

import com.handpay.launch.hp.R;

public enum SettingsIcon {

    SettingsIcon01("WIFI",R.color.color_wifi,R.drawable.setwifi),
    SettingsIcon02("显示",R.color.color_xianshi,R.drawable.setxianshi),
    SettingsIcon03("蓝牙",R.color.color_lanya,R.drawable.setlanya),
    SettingsIcon04("移动网络",R.color.color_yidongwangluo,R.drawable.setyidong),
    SettingsIcon05("VPN",R.color.color_vpn,R.drawable.setvpn),
    SettingsIcon06("设备信息",R.color.color_cunchuxinxi,R.drawable.setsave),
    SettingsIcon07("流量查看",R.color.color_liuliangchakan,R.drawable.setliuliang),
    SettingsIcon08("声音",R.color.color_sound,R.drawable.setvoice),
    SettingsIcon09("版本检测",R.color.color_banben,R.drawable.setbanben),
    SettingsIcon10("下载进度",R.color.color_download,R.drawable.set_download),
    SettingsIcon11("实时网速",R.color.color_sort,R.drawable.set_sort),
    SettingsIcon12("系统设置",R.color.color_system_setting,R.drawable.set_seticon),
    SettingsIcon13("退出",R.color.color_quite_app,R.drawable.set_exit),
    ;

    // 成员变量
    private String text;
    private int textColor;
    private int icon;

    // 构造方法
    SettingsIcon(String text, int textColor, int icon) {
        this.text = text;
        this.textColor = textColor;
        this.icon = icon;
    }

    // 普通方法
    public static int getTextColor(String text) {
        for (SettingsIcon c : SettingsIcon.values()) {
            if (c.getText().equals(text)) {
                return c.textColor;
            }
        }
        return 0;
    }

    // 普通方法
    public static int getIcon(String text) {
        for (SettingsIcon c : SettingsIcon.values()) {
            if (c.getText().equals(text)) {
                return c.icon;
            }
        }
        return 0;
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getIcon() {
        return icon;
    }
}