package com.woaiqw.pagerouter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sohu.generate.TestActivityBundle;
import com.sohu.hy.api.PageRouter;
import com.woaiqw.pagerouter.bean.ParcelableBean;
import com.woaiqw.pagerouter.bean.SerializableBean;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ArrayList<String> as = new ArrayList<>();
        as.add("android");
        as.add("ios");
        ArrayList<Integer> ai = new ArrayList<>();
        ai.add(100);
        ai.add(200);
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);

        PageRouter
            .of(this)
            .path("test")
            .setParams(new TestActivityBundle.Builder()
                            .setName('z')
                            .setMoney(50000)
                            .setBeanS(new SerializableBean("SerializableBean"))
                            .setBeanP(new ParcelableBean("ParcelableBean"))
                            .setStringArrayList(as)
                            .setIntegerArrayList(ai)
                            .setBm(bm)
                            .setMsg("HHHHH")
                            .bundle())
            .setUri(Uri.parse(""))
            .navigation();

    }
}
