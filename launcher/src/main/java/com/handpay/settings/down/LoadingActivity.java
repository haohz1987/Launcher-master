package com.handpay.settings.down;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.handpay.launch.hp.R;
import com.handpay.launch.util.LogT;
import com.handpay.launch.util.RxToast;
import com.handpay.settings.BaseActivity;
import com.handpay.view.ActionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试网速，取全局网速，同时会下载url_string文件
 */
public class LoadingActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener{
    private List<ApkModel> apks;
    private DownloadAdapter adapter;
    private OkDownload okDownload;
    private RecyclerView recyclerView;
    private List<Progress> progressList;
    private static boolean isTaskEnd = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_loading);
        super.onCreate(savedInstanceState);
        initDate();
        initAdapter();
        if(!isTaskEnd){
            startTask();
        }
    }
    private void startTask() {
        //从数据库中恢复数据
        progressList = DownloadManager.getInstance().getAll();
        OkDownload.restore(progressList);

        OkDownload.getInstance().setFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/HpAppStore/");
        OkDownload.getInstance().getThreadPool().setCorePoolSize(3);
        LogT.w(String.format("下载路径: %s", OkDownload.getInstance().getFolder()));

        for (ApkModel apk : apks) {
            //这里只是演示，表示请求可以传参，怎么传都行，和okgo使用方法一样
            GetRequest<File> request = OkGo.<File>get(apk.url)//
                    .headers("aaa", "111")//
                    .params("bbb", "222");

            //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
            OkDownload.request(apk.url, request)//
                    .priority(apk.priority)//
                    .extra1(apk)//
                    .save()
                    .register(new LogDownloadListener())//下载进度
                    .start();
            adapter.notifyDataSetChanged();
        }
//
//        okDownload = OkDownload.getInstance();
//        okDownload.addOnAllTaskEndListener(this);
//        okDownload.startAll();
    }

    private void initAdapter() {


        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DownloadAdapter(this);
        adapter.updateData(DownloadAdapter.TYPE_ALL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 下载数据源
     */
    private void initDate() {
        apks = new ArrayList<>();
        ApkModel apk1 = new ApkModel();
        apk1.name = "爱奇艺";
        apk1.iconUrl = "http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0c10c4c0155c9adf1282af008ed329378d54112ac";
        apk1.url = "http://121.29.10.1/f5.market.mi-img.com/download/AppStore/0b8b552a1df0a8bc417a5afae3a26b2fb1342a909/com.qiyi.video.apk";
        apks.add(apk1);
        ApkModel apk2 = new ApkModel();
        apk2.name = "微信";
        apk2.iconUrl = "http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/00814b5dad9b54cc804466369c8cb18f23e23823f";
        apk2.url = "http://116.117.158.129/f2.market.xiaomi.com/download/AppStore/04275951df2d94fee0a8210a3b51ae624cc34483a/com.tencent.mm.apk";
        apks.add(apk2);
        ApkModel apk3 = new ApkModel();
        apk3.name = "新浪微博";
        apk3.iconUrl = "http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/01db44d7f809430661da4fff4d42e703007430f38";
        apk3.url = "http://60.28.125.129/f1.market.xiaomi.com/download/AppStore/0ff41344f280f40c83a1bbf7f14279fb6542ebd2a/com.sina.weibo.apk";
        apks.add(apk3);
        ApkModel apk4 = new ApkModel();
        apk4.name = "QQ";
        apk4.iconUrl = "http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/072725ca573700292b92e636ec126f51ba4429a50";
        apk4.url = "http://121.29.10.1/f3.market.xiaomi.com/download/AppStore/0ff0604fd770f481927d1edfad35675a3568ba656/com.tencent.mobileqq.apk";
        apks.add(apk4);
        ApkModel apk5 = new ApkModel();
        apk5.name = "陌陌";
        apk5.iconUrl = "http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/06006948e655c4dd11862d060bd055b4fd2b5c41b";
        apk5.url = "http://121.18.239.1/f4.market.xiaomi.com/download/AppStore/096f34dec955dbde0597f4e701d1406000d432064/com.immomo.momo.apk";
        apks.add(apk5);
    }

    public ActionBar getActivityActionBar() {
        return new ActionBar("下载进度", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAllTaskEnd() {
        isTaskEnd = true;
        RxToast.showToast("所有下载任务已结束");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        progressList.clear();
        if(okDownload!=null){
            okDownload.removeOnAllTaskEndListener(this);
            okDownload=null;
        }
        adapter.unRegister();
    }


}
