package com.handpay.settings.down;

import com.handpay.launch.util.LogT;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;

public class LogDownloadListener extends DownloadListener {

    public LogDownloadListener() {
        super("LogDownloadListener");
    }

    @Override
    public void onStart(Progress progress) {
        LogT.w("onStart: " + progress);

    }

    @Override
    public void onProgress(Progress progress) {
        LogT.w("onProgress: " + progress);
    }

    @Override
    public void onError(Progress progress) {
        LogT.w("onError: " + progress);
        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(File file, Progress progress) {
        LogT.w("onFinish: " + progress);
    }

    @Override
    public void onRemove(Progress progress) {
        System.out.println("onRemove: " + progress);

    }
}
