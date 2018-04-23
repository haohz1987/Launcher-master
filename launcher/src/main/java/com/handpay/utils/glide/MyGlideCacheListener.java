package com.handpay.utils.glide;

import com.handpay.launch.util.LogT;

public class MyGlideCacheListener implements GlideCacheListener {
    @Override
    public void success(String path) {
    }

    @Override
    public void error(Exception e) {
        LogT.d("网络或缓存失败:"+e.getMessage());
    }
}
