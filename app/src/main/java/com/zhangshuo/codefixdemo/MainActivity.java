package com.zhangshuo.codefixdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //按钮点击
    public void gotoBusiness(View view) {
        //TODO 这里传入的0会做分母，会抛出zero by divide异常
        int result0 = calculate(10, 0);
        Log.i("CODEFIX", "result:" + result0);
    }
    //TODO 安装应用，并且赋予 存储卡权限

    //方法
    private int calculate(int i, int j) {
//        //修复代码
//        if(j==0) return 0;
        return i % j;
    }


    //在MainApplication中进行了修复
}
