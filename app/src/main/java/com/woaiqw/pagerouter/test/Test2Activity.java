package com.woaiqw.pagerouter.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sohu.hy.annotation.Route;
import com.woaiqw.pagerouter.R;
import com.woaiqw.pagerouter.common.Constants;


/**
 * Created by haoran on 2019/7/31.
 */

@Route(path = Constants.RouterPath.TEST2)
public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_2);

    }
}