package com.woaiqw.pagerouter;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sohu.generate.TestActivityBundle;
import com.sohu.hy.api.PageRouter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PageRouter
            .of(this)
            .path("test")
            .setParams(new TestActivityBundle.Builder().setMsg("ssss").bundle())
            .setUri(Uri.parse(""))
            .navigation();
    }
}
