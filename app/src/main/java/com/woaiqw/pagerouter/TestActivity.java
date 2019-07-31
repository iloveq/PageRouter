package com.woaiqw.pagerouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sohu.hy.annotation.Args;
import com.sohu.hy.annotation.Route;
import com.sohu.hy.api.BundleService;

/**
 * Created by haoran on 2019/7/31.
 */
@Route(path = "test")
public class TestActivity extends AppCompatActivity {

    @Args
    public String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BundleService.bind(this);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}