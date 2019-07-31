package com.sohu.hy.api;

import android.app.Activity;

import java.lang.reflect.Method;

public class BundleService {

    public static void bind(Activity target) {
        try{
            String bundlePath = "com.sohu.generate." + target.getClass().getSimpleName() + "Bundle";
            Class<?> c = Class.forName(bundlePath);
            Method bind = c.getMethod("bind", target.getClass());
            bind.invoke(null, target);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
