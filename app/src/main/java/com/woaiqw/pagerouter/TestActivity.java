package com.woaiqw.pagerouter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sohu.hy.annotation.Args;
import com.sohu.hy.annotation.Route;
import com.sohu.hy.api.BundleService;
import com.woaiqw.pagerouter.bean.ParcelableBean;
import com.woaiqw.pagerouter.bean.SerializableBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoran on 2019/7/31.
 */
@Route(path = "test")
public class TestActivity extends AppCompatActivity {

    @Args
    public String msg;

    @Args
    public Character character;

    @Args
    public double aDouble;

    @Args
    public SerializableBean serializableBean;

    @Args
    public ParcelableBean parcelableBean;

//    @Args
//    public NormalBean normalBean;

//    @Args
//    public List<String> stringList;

    @Args
    public Bitmap bm;

    @Args
    public ArrayList<SerializableBean> serializableBeanArrayList;

    @Args
    public ArrayList<String> stringArrayList;

    @Args
    public ArrayList<Integer> integerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BundleService.bind(this);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}