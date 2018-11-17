package com.zhangshuo.codefixdemo;

import android.app.Application;
import android.widget.Toast;

/**
 * 作者: 张杰
 * 时间: 2018/11/17
 * 描述:
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FixUtils fixUtils = new FixUtils();
        boolean isFixed = fixUtils.loadFixedDex(this, "/sdcard/1.dex");
        Toast.makeText(this, "修复结果:" + isFixed, Toast.LENGTH_SHORT).show();
    }
}
