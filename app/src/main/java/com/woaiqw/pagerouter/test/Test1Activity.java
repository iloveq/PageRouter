package com.woaiqw.pagerouter.test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.hy.annotation.Args;
import com.sohu.hy.annotation.Route;
import com.sohu.hy.api.BundleService;
import com.woaiqw.pagerouter.R;
import com.woaiqw.pagerouter.bean.ParcelableBean;
import com.woaiqw.pagerouter.bean.SerializableBean;
import com.woaiqw.pagerouter.common.Constants;

import java.util.ArrayList;


/**
 * Created by haoran on 2019/7/31.
 */
@Route(path = Constants.RouterPath.TEST1)
public class Test1Activity extends AppCompatActivity {

    @Args
    public String msg;

    @Args
    public Character name;

    @Args
    public double money;

    @Args
    public SerializableBean beanS;

    @Args
    public ParcelableBean beanP;

//    @Args
//    public NormalBean normalBean;

//    @Args
//    public List<String> stringList;

    @Args
    public Bitmap bm;

    @Args
    public ArrayList<String> stringArrayList;

    @Args
    public ArrayList<Integer> integerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1);
        BundleService.bind(this);

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        TextView tv = findViewById(R.id.tv);
        ImageView iv = findViewById(R.id.iv);

        String content = "name:"+name
                +"\n"
                +"money:"+money
                +"\n"
                +"serializable:"
                +"\n"
                +beanS.type
                +"\n"
                +"parcelable:"
                +"\n"
                +beanP.type
                +"\n"
                + "stringArrayList:"+ stringArrayList.toString()
                +"\n"
                +"integerArrayList:"+integerArrayList.toString();

        tv.setText(content);

        iv.setImageBitmap(bm);

    }
}