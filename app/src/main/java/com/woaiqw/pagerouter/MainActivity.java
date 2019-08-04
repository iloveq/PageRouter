package com.woaiqw.pagerouter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.sohu.generate.Test1ActivityBundle;
import com.sohu.hy.api.PageRouter;
import com.woaiqw.pagerouter.bean.ParcelableBean;
import com.woaiqw.pagerouter.bean.SerializableBean;
import com.woaiqw.pagerouter.common.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv1).setOnClickListener(this);
        findViewById(R.id.tv2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.tv1){

            ArrayList<String> as = new ArrayList<>();
            as.add("android");
            as.add("ios");
            ArrayList<Integer> ai = new ArrayList<>();
            ai.add(100);
            ai.add(200);
            Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.flutter);

            PageRouter
                    .of(this)
                    .path(Constants.RouterPath.TEST1)
                    .setParams(new Test1ActivityBundle.Builder()
                            .setName('z')
                            .setMoney(50000)
                            .setBeanS(new SerializableBean("SerializableBean"))
                            .setBeanP(new ParcelableBean("ParcelableBean"))
                            .setStringArrayList(as)
                            .setIntegerArrayList(ai)
                            .setBm(bm)
                            .setMsg("Welcome2Test1Activity")
                            .bundle())
                    .setUri(Uri.parse(""))
                    .navigation();

            return;

        }

        if (v.getId()==R.id.tv2){

            PageRouter
                    .of(this)
                    .path(Constants.RouterPath.TEST2)
                    .navigation();

        }



    }
}
